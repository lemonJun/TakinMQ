package com.lemon.takinmq.client.test;

import org.apache.log4j.PropertyConfigurator;

import com.alibaba.fastjson.JSON;
import com.lemon.takinmq.client.producer.DefaultMQProducer;
import com.lemon.takinmq.client.producer.MQProducer;
import com.lemon.takinmq.common.message.Message;
import com.lemon.takinmq.common.message.SendResult;
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
            msg.setBody("haha2".getBytes());
            SendResult result = producer.send(msg);
            System.out.println(JSON.toJSONString(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
