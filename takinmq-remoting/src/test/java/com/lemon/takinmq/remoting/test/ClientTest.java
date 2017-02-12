package com.lemon.takinmq.remoting.test;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;

import com.lemon.takinmq.remoting.netty5.RemotingNettyClient;
import com.lemon.takinmq.remoting.netty5.NettyClientConfig;
import com.lemon.takinmq.remoting.netty5.RemotingMessage;

public class ClientTest {

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("D:/log4j.properties");

            NettyClientConfig config = new NettyClientConfig();
            RemotingNettyClient client = new RemotingNettyClient(config);
            client.start();
            RemotingMessage msg = new RemotingMessage();
            for (int i = 0; i < 10; i++) {
                client.invokeSync("127.0.0.1:6871", msg, 3000);
                TimeUnit.SECONDS.sleep(1);
            }
            System.out.println(msg.getResultJson());
        } catch (Exception e) {
        }
    }

}
