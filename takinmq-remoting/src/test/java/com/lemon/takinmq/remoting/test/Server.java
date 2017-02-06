package com.lemon.takinmq.remoting.test;

import com.lemon.takinmq.remoting.netty5.NettyServer;

public class Server {

    public static void main(String[] args) {
        NettyServer server = new NettyServer();
        try {
            server.bind(6871);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
