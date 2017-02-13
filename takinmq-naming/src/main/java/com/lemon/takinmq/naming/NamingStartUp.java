package com.lemon.takinmq.naming;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.testng.internal.thread.ThreadUtil.ThreadFactoryImpl;

import com.lemon.takinmq.common.ImoduleService;
import com.lemon.takinmq.naming.routeinfo.RouteInfoManager;
import com.lemon.takinmq.remoting.netty5.NettyServerConfig;
import com.lemon.takinmq.remoting.netty5.RemotingNettyServer;

/**
 * 启动服务服务注册模块
 *
 * @author WangYazhou
 * @date  2017年2月7日 下午2:10:36
 * @see
 */
public class NamingStartUp implements ImoduleService {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl("NSScheduledThread"));

    private final RouteInfoManager routeInfoManager;

    private RemotingNettyServer remotingserver;

    private final NettyServerConfig serverConfig;

    public NamingStartUp(NettyServerConfig serverconfig) {
        this.serverConfig = serverconfig;
        this.routeInfoManager = new RouteInfoManager();
    }

    @Override
    public void init() throws Exception {
        //初始化监听服务
        //
        this.loadconfig();
        remotingserver = new RemotingNettyServer(serverConfig);

        //启动jgroups的群组通信   好处是任何组件连接任何一个name都可以获取所有配置信息 

        //
        //定时检查非活跃的broker
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                NamingStartUp.this.routeInfoManager.scanNotActiveBroker();
            }
        }, 60, 10, TimeUnit.SECONDS);
        
        //定时打印出此name服务中的信息
        //        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
        //            @Override
        //            public void run() {
        //                NamingStartUp.this.routeInfoManager.printAllPeriodically();
        //            }
        //        }, 60, 30, TimeUnit.SECONDS);
    }

    @Override
    public void start() throws Exception {
        remotingserver.start();//启动监听
    }

    @Override
    public void shutdown() throws Exception {

    }

    @Override
    public void loadconfig() {

    }

}
