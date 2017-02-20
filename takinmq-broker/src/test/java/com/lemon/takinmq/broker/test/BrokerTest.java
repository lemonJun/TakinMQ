package com.lemon.takinmq.broker.test;

import org.apache.log4j.PropertyConfigurator;

import com.lemon.takinmq.broker.BrokerStartUp;
import com.lemon.takinmq.common.BrokerConfig;
import com.lemon.takinmq.remoting.netty5.NettyClientConfig;
import com.lemon.takinmq.remoting.netty5.NettyServerConfig;
import com.lemon.takinmq.store.config.MessageStoreConfig;

public class BrokerTest {

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("D:/log4j.properties");
            BrokerConfig brokerConfig = new BrokerConfig();
            brokerConfig.setBrokerClusterName("Test");
            brokerConfig.setBrokerId(1000L);
            brokerConfig.setBrokerIP1("127.0.0.1");
            NettyServerConfig serverConfig = new NettyServerConfig();
            serverConfig.setListenPort(6876);

            NettyClientConfig clientConfig = new NettyClientConfig();
            MessageStoreConfig messageConfig = new MessageStoreConfig();
            BrokerStartUp broker = new BrokerStartUp(brokerConfig, serverConfig, clientConfig, messageConfig);
            broker.init();
            broker.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
