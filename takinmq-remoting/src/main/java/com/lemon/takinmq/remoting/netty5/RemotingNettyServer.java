package com.lemon.takinmq.remoting.netty5;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.lemon.takinmq.common.util.SystemClock;
import com.lemon.takinmq.remoting.util.GenericsUtils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RemotingNettyServer {

    private static final Logger logger = Logger.getLogger(RemotingNettyServer.class);

    private final ServerBootstrap bootstrap = new ServerBootstrap();
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final ScheduledExecutorService respScheduler;
    private final NettyServerConfig serverconfig;
    public static final ConcurrentHashMap<Integer, ResponseFuture> responseTable = GenericsUtils.newConcurrentHashMap();

    public RemotingNettyServer(final NettyServerConfig serverconfig) {
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        respScheduler = new ScheduledThreadPoolExecutor(1);
        this.serverconfig = serverconfig;
    }

    public void start() throws Exception {
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 65536);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.localAddress(serverconfig.getListenPort());
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws IOException {
                //                ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(60, 60, 60));
                //                ch.pipeline().addLast("heartbeat", new CustomIdleHandler());
                ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                ch.pipeline().addLast("remoteinvode", new RemotingInvokeHandler());
            }
        });

        this.bootstrap.bind(6871).sync();
        logger.info("server started on port:" + serverconfig.getListenPort());
        respScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                scanResponseTable(5000);
            }
        }, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * 描述所有的对外请求
     * 看是否有超时未处理情况
     * 此超时时间已经考虑了 任务的默认等待时间
     */
    public void scanResponseTable(long timeout) {
        Iterator<Entry<Integer, ResponseFuture>> it = responseTable.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Integer, ResponseFuture> next = it.next();
            ResponseFuture rep = next.getValue();

            if ((rep.getBeginTimestamp() + rep.getTimeoutMillis() + timeout) <= SystemClock.now()) {
                logger.info(String.format("remove responsefuture %s ", rep.getOpaque()));
                it.remove();
            }
        }
    }

    /**
     * 监听到虚拟机停止时调用
     * 当系统启动失败时也调用
     */
    public void shutdown() {
        try {
            logger.info("receive shutdown listener ");
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
                logger.info("shotdown bossGroup");
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
                logger.info("shotdown workerGroup");
            }
            if (respScheduler != null) {
                respScheduler.shutdown();
                logger.info("shotdown respScheduler");
            }
        } catch (Exception e) {
            logger.error("shutsown error", e);
        }

    }
}
