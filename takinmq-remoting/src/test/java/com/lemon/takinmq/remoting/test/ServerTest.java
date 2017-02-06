package com.lemon.takinmq.remoting.test;

import org.apache.log4j.PropertyConfigurator;

import com.lemon.takinmq.remoting.netty5.NettyServer;

public class ServerTest {

    public static void main(String[] args) {
        PropertyConfigurator.configure("D:/log4j.properties");
        NettyServer server = new NettyServer();
        try {
            server.bind(6871);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
