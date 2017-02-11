package com.lemon.takinmq.broker.client;

import com.lemon.takinmq.remoting.netty5.NettyClientConfig;
import com.lemon.takinmq.remoting.netty5.RemotingNettyClient;

/**
 * broker对外调用的API封装
 *
 * @author WangYazhou
 * @date  2017年2月11日 下午6:11:31
 * @see
 */
public class BrokerOuterAPI {

    private RemotingNettyClient remotingNettyClient;

    public BrokerOuterAPI(final NettyClientConfig nettyClientConfig) {
        this.remotingNettyClient = new RemotingNettyClient(nettyClientConfig);
    }

    //获取naming的地址 并更新本地缓存
    //改用jgroups组件后  这部分应该不需要了
    public void fetchNameServerAddr() {

    }

}
