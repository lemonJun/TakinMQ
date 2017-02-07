package com.lemon.takinmq.remoting.test;

import org.apache.log4j.PropertyConfigurator;

import com.lemon.takinmq.remoting.netty5.NettyServerConfig;
import com.lemon.takinmq.remoting.netty5.RemotingNettyServer;

public class ServerTest {

    public static void main(String[] args) {
        PropertyConfigurator.configure("D:/log4j.properties");
        try {
            NettyServerConfig config = new NettyServerConfig();
            config.setListenPort(6871);
            RemotingNettyServer server = new RemotingNettyServer(config);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
