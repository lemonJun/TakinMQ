/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.lemon.takinmq.broker.client;

import java.util.List;

import com.lemon.takinmq.broker.BrokerStartUp;

import io.netty.channel.Channel;

/**
 * @author lemon
 */
public class DefaultConsumerIdsChangeListener implements ConsumerIdsChangeListener {
    private final BrokerStartUp brokerStartUp;

    public DefaultConsumerIdsChangeListener(BrokerStartUp brokerStartUp) {
        this.brokerStartUp = brokerStartUp;
    }

    @Override
    public void consumerIdsChanged(String group, List<Channel> channels) {
        if (channels != null && brokerStartUp.getBrokerConfig().isNotifyConsumerIdsChangedEnable()) {
            for (Channel chl : channels) {
                //                this.brokerStartUp.getBroker2Client().notifyConsumerIdsChanged(chl, group);
            }
        }
    }
}
