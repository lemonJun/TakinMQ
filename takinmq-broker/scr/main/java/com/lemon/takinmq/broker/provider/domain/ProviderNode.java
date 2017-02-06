package com.lemon.takinmq.remoting.provider.domain;

import com.lemon.takinmq.remoting.core.cluster.Node;
import com.lemon.takinmq.remoting.core.cluster.NodeType;

/**
 * @author Robert HG (254963746@qq.com) on 7/23/14.
 * Job Tracker 节点
 */
public class ProviderNode extends Node {

    public ProviderNode() {
        this.setNodeType(NodeType.CONSUMER_NODE);
    }
}
