package com.lemon.takinmq.remoting.consumer;

import java.util.concurrent.atomic.AtomicBoolean;

import com.lemon.takinmq.remoting.io.netty5.NettyClient;

/**
 * 
 * NETTY的一个代理类   用于完成远程调用服务
 * 后面所有对job server的调用 都需要通过此类来完成
 * 
 * @author lemon
 * @version 1.0
 * @date  2015年9月18日 下午2:08:23
 * @see   
 * @since 
 */
public class NettyClientProxy {

    private NettyClient client;

    /**
     * 因客户端连接的特性  需要防止多次初始化
     */
    private final AtomicBoolean clientSwitch = new AtomicBoolean(false);

    public void init() {
        if (clientSwitch.compareAndSet(false, true)) {
            client = new NettyClient();
            client.start();
        }
    }

    public NettyClient getClient() {
        return client;
    }

}
