package com.lemon.takinmq.remoting.provider;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lemon.takinmq.remoting.commons.GuiceDI;
import com.lemon.takinmq.remoting.core.cluster.Config;
import com.lemon.takinmq.remoting.io.netty5.NettyServer;
import com.lemon.takinmq.remoting.provider.channel.ChannelManager;
import com.lemon.takinmq.remoting.provider.registry.ProviderRegistryManager;
import com.lemon.takinmq.remoting.util.Constants;

/**
 * provider服务端启动入口
 *
 * 
 * @author lemon
 * @version 1.0
 * @date  2016年3月1日 下午5:18:02
 * @see 
 * @since
 */
public class ProviderMain {

    private static final Logger logger = LoggerFactory.getLogger(ProviderMain.class);

    private NettyServerConfig serverConfig;
    private Config config;

    public static void main(String[] args) {
        final ProviderMain main = new ProviderMain();
        main.start();
    }

    /**
     * 
     */
    public void start() {
        try {
            initConfig();//初始化配置
            initRegistry();//启动注册服务
            initCheck();//启动后台检查
            startNettyServer();//启动此服务
            addhook();//添加关闭勾子
            logger.info("");
            logger.info(String.format("%sJob Server Start Success with port = %d", Constants.LOGTIP, serverConfig.getListenPort()));
        } catch (Exception e) {
            e.printStackTrace();
            //启动失败 记得关闭已启服务
            try {
                stopAll();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                Thread.sleep(3000);
                logger.info("");
                logger.info(String.format("%sUse It Well.%s", Constants.LOGTIP, Constants.LOGTIP));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * 初始化配置文件
     * 初始化系统配置serverconfig
     * 初始化业务配置config
     */
    final private void initConfig() throws Exception {
        PropertyConfigurator.configure("D:/log4j.properties");
        //SCFInit.init("E:/opt/wf/com.bj58.zhaopin.web.foresee/scf.config");
        logger.info("");
        int listenport = 6062;
        serverConfig = GuiceDI.getInstance(NettyServerConfig.class);
        config = GuiceDI.getInstance(Config.class);
        serverConfig.setListenPort(listenport);
        config.setListenPort(listenport);
        //        GuiceDI.getInstance(CuratorZKClient.class);
        Thread.sleep(3000);//等zk初始化完成 TODO 改为监听
        logger.info(String.format("%sInit Config Success%s", Constants.LOGTIP, Constants.LOGTIP));
    }

    /**
     * 1 实现ZK的注册中心
     * 2 监听每个节点的变化
     * 3 监听同类别节点主从的变化
     * 4 清空僵尸任务  只能有
     * @throws Exception
     */
    final private void initRegistry() throws Exception {
        GuiceDI.getInstance(ProviderRegistryManager.class).start();
        logger.info(String.format("%sInit Registry Success%s", Constants.LOGTIP, Constants.LOGTIP));
    }

    /**
     * 执行后台检查任务
     * 这个是任务一个Server都需要做的
     */
    final private void initCheck() throws Exception {
        GuiceDI.getInstance(ChannelManager.class).start();
        logger.info(String.format("%sInit Check Success%s", Constants.LOGTIP, Constants.LOGTIP));
    }

    /**
     * 启动NETTY服务端
     */
    final private void startNettyServer() throws Exception {
        GuiceDI.getInstance(NettyServer.class).bind();
        logger.info(String.format("%sStart Netty Success%s", Constants.LOGTIP, Constants.LOGTIP));
    }

    /**
     * 停止
     */
    final private void addhook() throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    stopAll();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }));
        logger.info(String.format("%sAdd Stophook %s", Constants.LOGTIP, Constants.LOGTIP));
    }

    final private void stopAll() throws Exception {
        GuiceDI.getInstance(NettyServer.class).shutdown();
        GuiceDI.getInstance(ChannelManager.class).stop();
        GuiceDI.getInstance(ProviderRegistryManager.class).stop();
        logger.info(String.format("Job Server Stopped "));
    }
}
