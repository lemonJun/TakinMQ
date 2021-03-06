/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.takin.mq.broker;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.mq.broker.TopicCommand.TaskType;
import com.takin.mq.cluster.Broker;
import com.takin.mq.utils.MQ;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.zkclient.IZkStateListener;
import com.takin.rpc.zkclient.ZkClient;
import com.takin.rpc.zkclient.ZkUtils;
import com.takin.rpc.zkclient.exception.ZkNodeExistsException;

/**
 * Handles the server's interaction with zookeeper. The server needs to register the following
 * paths:
 *
 * <pre>
 *   /topics/[topic]/[node_id-partition_num]
 *   /brokers/[0...N] -- host:port
 * </pre>
 *
 * @author adyliu (imxylz@gmail.com)
 * @since 1.0
 */
public class ServerRegister implements IZkStateListener, Closeable {

    private static final Logger logger = LoggerFactory.getLogger(ServerRegister.class);

    private final String brokerIdPath;

    private ZkClient zkClient;

    private Set<String> topics = new LinkedHashSet<String>();

    private final Object lock = new Object();

    BrokerConfig config = null;

    public ServerRegister() {
        config = GuiceDI.getInstance(BrokerConfig.class);
        this.brokerIdPath = MQ.BrokerIdsPath + "/" + GuiceDI.getInstance(BrokerConfig.class).getBrokerid();
    }

    public void startup() {
        logger.info("connecting to zookeeper: " + config.getZkhosts());
        zkClient = new ZkClient(config.getZkhosts(), config.getZksessiontimeoutms(), config.getZookeeperconnectiontimeoutms());
        zkClient.subscribeStateChanges(this);
    }
    
    public void processTask(TopicCommand task) {
        final String topicPath = MQ.BrokerTopicsPath + "/" + task.topic;
        final String brokerTopicPath = MQ.BrokerTopicsPath + "/" + task.topic + "/" + config.getBrokerid();
        synchronized (lock) {
            switch (task.type) {
                case DELETE:
                    topics.remove(task.topic);
                    ZkUtils.deletePath(zkClient, brokerTopicPath);
                    List<String> brokers = ZkUtils.getChildrenParentMayNotExist(zkClient, topicPath);
                    if (brokers != null && brokers.size() == 0) {
                        ZkUtils.deletePath(zkClient, topicPath);
                    }
                    break;
                case CREATE:
                    topics.add(task.topic);
                    ZkUtils.createEphemeralPathExpectConflict(zkClient, brokerTopicPath, "" + getPartitions(task.topic));
                    break;
                case ENLARGE:
                    ZkUtils.deletePath(zkClient, brokerTopicPath);
                    ZkUtils.createEphemeralPathExpectConflict(zkClient, brokerTopicPath, "" + getPartitions(task.topic));
                    break;
                default:
                    logger.error("unknow task: " + task);
                    break;
            }
        }
    }

    private int getPartitions(String topic) {
        int numParts = config.getNumpartitions();
        return numParts;
    }

    /**
     * register broker in the zookeeper
     * <p>
     * path: /brokers/ids/&lt;id&gt; <br>
     * data: creator:host:port
     */
    public void registerBrokerInZk() {
        logger.info("Registering broker " + brokerIdPath);
        String hostname = config.getHostname();
        if (hostname == null) {
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                throw new RuntimeException("cannot get local host, setting 'hostname' in configuration");
            }
        }
        //
        final String creatorId = hostname + "-" + System.currentTimeMillis();
        final Broker broker = new Broker(config.getBrokerid(), creatorId, hostname, config.getPort(), config.isTopicAutoCreated());
        try {
            ZkUtils.createEphemeralPathExpectConflict(zkClient, brokerIdPath, broker.getZKString());
        } catch (ZkNodeExistsException e) {
            String oldServerInfo = ZkUtils.readDataMaybeNull(zkClient, brokerIdPath);
            String message = "A broker (%s) is already registered on the path %s." //
                            + " This probably indicates that you either have configured a brokerid that is already in use, or "//
                            + "else you have shutdown this broker and restarted it faster than the zookeeper " ///
                            + "timeout so it appears to be re-registering.";
            message = String.format(message, oldServerInfo, brokerIdPath);
            throw new RuntimeException(message);
        }
        //
        logger.info("Registering broker " + brokerIdPath + " succeeded with " + broker);
    }

    /**
     *
     */
    public void close() {
        if (zkClient != null) {
            logger.info("closing zookeeper client...");
            zkClient.close();
        }
    }

    public void handleNewSession() throws Exception {
        logger.info("re-registering broker info in zookeeper for broker " + config.getBrokerid());
        registerBrokerInZk();
        synchronized (lock) {
            logger.info("re-registering broker topics in zookeeper for broker " + config.getBrokerid());
            for (String topic : topics) {
                processTask(new TopicCommand(TaskType.CREATE, topic));
            }
        }

    }

    public void handleStateChanged(KeeperState state) throws Exception {
    }
}
