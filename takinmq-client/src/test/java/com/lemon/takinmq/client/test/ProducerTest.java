package com.lemon.takinmq.client.test;

import org.apache.log4j.PropertyConfigurator;

import com.lemon.takinmq.client.producer.DefaultMQProducer;
import com.lemon.takinmq.client.producer.MQProducer;
import com.lemon.takinmq.common.message.Message;
import com.lemon.takinmq.remoting.netty5.NettyClientConfig;

public class ProducerTest {

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("D:/log4j.properties");

            NettyClientConfig clientConfig = new NettyClientConfig();
            MQProducer producer = new DefaultMQProducer(clientConfig);
            producer.start();
            Message msg = new Message();
            msg.setTopic("Test");
            msg.setBody("haha".getBytes());
            producer.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
