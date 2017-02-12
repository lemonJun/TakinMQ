package com.lemon.takinmq.naming.test;

import org.apache.log4j.PropertyConfigurator;

import com.lemon.takinmq.naming.NamingStartUp;
import com.lemon.takinmq.remoting.netty5.NettyServerConfig;

public class NamingTest {

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("D:/log4j.properties");
            NettyServerConfig serverConfig = new NettyServerConfig();
            serverConfig.setListenPort(5871);
            NamingStartUp naming = new NamingStartUp(serverConfig);
            naming.init();
            naming.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
