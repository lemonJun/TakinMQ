package com.lemon.takinmq.remoting.consumer;

import com.lemon.takinmq.remoting.core.cluster.Node;

/**
 * @author Robert HG (254963746@qq.com) on 8/14/14.
 * TaskTracker 节点
 */
public class Consumer extends Node {

    public Consumer() {
        //        this.setNodeType(NodeType.TASK_TRACKER);
        //        // 关注 JobTracker
        //        this.addListenNodeType(NodeType.JOB_TRACKER.toString());

        //不需要关注自已
        //        this.addListenNodeType(NodeType.TASK_TRACKER.toString());
    }

}
