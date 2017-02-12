package com.lemon.takinmq.broker.topic;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lemon.takinmq.broker.BrokerStartUp;
import com.lemon.takinmq.common.DataVersion;
import com.lemon.takinmq.common.TopicConfig;
import com.lemon.takinmq.common.datainfo.KVTable;
import com.lemon.takinmq.common.datainfo.TopicConfigSerializeWrapper;

/**
 * topic配置管理
 * 仅仅是一个配置管理  为什么还要单独起一个类呢？
 * 因为一个broker中有太多类型的主题,如测试主题   默认主题(不指定) benchmark主题   以集群作主题   brokename主师
 * 因为太多了  所以
 *
 * @author WangYazhou
 * @date  2017年2月10日 下午12:24:27
 * @see
 */
public class TopicConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(TopicConfigManager.class);

    private transient BrokerStartUp brokerStartUp;

    private final ConcurrentHashMap<String, TopicConfig> topicConfigTable = new ConcurrentHashMap<String, TopicConfig>(1024);
    private final DataVersion dataVersion = new DataVersion();

    public TopicConfigManager() {

    }

    //加入各种主题
    public TopicConfigManager(BrokerStartUp brokerStartUp) {
        this.brokerStartUp = brokerStartUp;
    }

    public TopicConfigSerializeWrapper buildTopicConfigSerializeWrapper() {
        TopicConfigSerializeWrapper topicConfigSerializeWrapper = new TopicConfigSerializeWrapper();
        topicConfigSerializeWrapper.setTopicConfigTable(this.topicConfigTable);
        topicConfigSerializeWrapper.setDataVersion(this.dataVersion);
        return topicConfigSerializeWrapper;
    }

    public void updateOrderTopicConfig(final KVTable orderKVTableFromNs) {

        if (orderKVTableFromNs != null && orderKVTableFromNs.getTable() != null) {
            boolean isChange = false;
            Set<String> orderTopics = orderKVTableFromNs.getTable().keySet();
            for (String topic : orderTopics) {
                TopicConfig topicConfig = this.topicConfigTable.get(topic);
                if (topicConfig != null && !topicConfig.isOrder()) {
                    topicConfig.setOrder(true);
                    isChange = true;
                    logger.info("update order topic config, topic={}, order={}", topic, true);
                }
            }

            for (Map.Entry<String, TopicConfig> entry : this.topicConfigTable.entrySet()) {
                String topic = entry.getKey();
                if (!orderTopics.contains(topic)) {
                    TopicConfig topicConfig = entry.getValue();
                    if (topicConfig.isOrder()) {
                        topicConfig.setOrder(false);
                        isChange = true;
                        logger.info("update order topic config, topic={}, order={}", topic, false);
                    }
                }
            }

            if (isChange) {
                this.dataVersion.nextVersion();
                this.persist();
            }
        }
    }

    private void persist() {

    }

}
