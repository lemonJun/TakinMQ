package com.lemon.takinmq.broker.client;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lemon.takinmq.common.datainfo.TopicConfigSerializeWrapper;
import com.lemon.takinmq.common.naming.RegisterBrokerResult;
import com.lemon.takinmq.common.service.INamingService;
import com.lemon.takinmq.remoting.clientproxy.JDKProxy;
import com.lemon.takinmq.remoting.exception.MQBrokerException;
import com.lemon.takinmq.remoting.exception.RemotingCommandException;
import com.lemon.takinmq.remoting.exception.RemotingConnectException;
import com.lemon.takinmq.remoting.exception.RemotingSendRequestException;
import com.lemon.takinmq.remoting.exception.RemotingTimeoutException;
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

    private static final Logger logger = LoggerFactory.getLogger(BrokerOuterAPI.class);

    private final RemotingNettyClient remotingNettyClient;

    private final JDKProxy proxy;

    public BrokerOuterAPI(final NettyClientConfig nettyClientConfig) {
        this.remotingNettyClient = new RemotingNettyClient(nettyClientConfig);
        proxy = new JDKProxy(remotingNettyClient);
    }

    public void start() {
        remotingNettyClient.start();
    }

    //获取naming的地址 并更新本地缓存
    //改用jgroups组件后  这部分应该不需要了
    public void fetchNameServerAddr() {

    }

    /**
     * 
     * @param clusterName 集群名称
     * @param brokerAddr  本broker地址
     * @param brokerName  本broker名称
     * @param brokerId    本brokerid
     * @param haServerAddr ha地址
     * @param topicConfigWrapper  topic配置
     * @param filterServerList  过滤服务
     * @param oneway
     * @param timeoutMills
     * @return
     */
    public RegisterBrokerResult registerBrokerAll(final String clusterName, final String brokerAddr, final String brokerName, final long brokerId, final String haServerAddr, final TopicConfigSerializeWrapper topicConfigWrapper, final List<String> filterServerList, final boolean oneway, final int timeoutMills) {
        RegisterBrokerResult registerBrokerResult = null;

        //
        List<String> nameServerAddressList = this.remotingNettyClient.getNameServerAddressList();
        if (nameServerAddressList != null) {
            for (String namesrvAddr : nameServerAddressList) {
                try {
                    RegisterBrokerResult result = this.registerBroker(namesrvAddr, clusterName, brokerAddr, brokerName, brokerId, haServerAddr, topicConfigWrapper, filterServerList, oneway, timeoutMills);
                    if (result != null) {
                        registerBrokerResult = result;
                    }

                    logger.info("register broker to name server {} OK", namesrvAddr);
                } catch (Exception e) {
                    logger.warn("registerBroker Exception, " + namesrvAddr, e);
                }
            }
        }

        return registerBrokerResult;
    }

    /**
     * 向一个naming服务注册broker
     * 原实现是直接发起remoting调用->更改之后的实现是由 Proxy调用
     * @param namesrvAddr naming地址
     * @param clusterName 集群名称
     * @param brokerAddr  本broker地址
     * @param brokerName  broker名称
     * @param brokerId    brokerid
     * @param haServerAddr ha地址
     * @param topicConfigWrapper topic配置
     * @param filterServerList 过滤服务列表
     * @param oneway  是否是单向调用
     * @param timeoutMills  超时时间
     * @return
     * @throws RemotingCommandException
     * @throws MQBrokerException
     * @throws RemotingConnectException
     * @throws RemotingSendRequestException
     * @throws RemotingTimeoutException
     * @throws InterruptedException
     */
    private RegisterBrokerResult registerBroker(final String namesrvAddr, final String clusterName, final String brokerAddr, final String brokerName, final long brokerId, final String haServerAddr, final TopicConfigSerializeWrapper topicConfigWrapper, final List<String> filterServerList, final boolean oneway, final int timeoutMills) throws RemotingCommandException, MQBrokerException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, InterruptedException {
        //        RegisterBrokerRequestHeader requestHeader = new RegisterBrokerRequestHeader();
        //        requestHeader.setBrokerAddr(brokerAddr);
        //        requestHeader.setBrokerId(brokerId);
        //        requestHeader.setBrokerName(brokerName);
        //        requestHeader.setClusterName(clusterName);
        //        requestHeader.setHaServerAddr(haServerAddr);
        //        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.REGISTER_BROKER, requestHeader);
        //
        //        RegisterBrokerBody requestBody = new RegisterBrokerBody();
        //        requestBody.setTopicConfigSerializeWrapper(topicConfigWrapper);
        //        requestBody.setFilterServerList(filterServerList);
        //        request.setBody(requestBody.encode());

        //
        INamingService namingProxy = proxy.createProxy(INamingService.class, "INamingService");

        //        if (oneway) {
        //            try {
        //                this.remotingNettyClient.invokeOneway(namesrvAddr, request, timeoutMills);
        //            } catch (RemotingTooMuchRequestException e) {
        //            }
        //            return null;
        //        }

        try {
            RegisterBrokerResult result = namingProxy.register(clusterName, brokerAddr, brokerName, brokerId, topicConfigWrapper);
            return result;
        } catch (Exception e) {
            throw new MQBrokerException("broker register errror", e);
        }
        //        RemotingCommand response = this.remotingClient.invokeSync(namesrvAddr, request, timeoutMills);
        //        assert response != null;
        //        switch (response.getCode()) {
        //            case ResponseCode.SUCCESS: {
        //                RegisterBrokerResponseHeader responseHeader = (RegisterBrokerResponseHeader) response.decodeCommandCustomHeader(RegisterBrokerResponseHeader.class);
        //                RegisterBrokerResult result = new RegisterBrokerResult();
        //                result.setMasterAddr(responseHeader.getMasterAddr());
        //                result.setHaServerAddr(responseHeader.getHaServerAddr());
        //                result.setHaServerAddr(responseHeader.getHaServerAddr());
        //                if (response.getBody() != null) {
        //                    result.setKvTable(KVTable.decode(response.getBody(), KVTable.class));
        //                }
        //                return result;
        //            }
        //            default:
        //                break;
        //        }

    }

    public void updateNameServerAddressList(final String addrs) {
        List<String> lst = new ArrayList<String>();
        String[] addrArray = addrs.split(";");
        if (addrArray != null) {
            for (String addr : addrArray) {
                lst.add(addr);
            }
            this.remotingNettyClient.updateNameServerAddressList(lst);
        }
    }

}
