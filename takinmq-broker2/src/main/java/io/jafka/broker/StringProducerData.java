
package io.jafka.broker;

import java.util.ArrayList;
import java.util.List;

public class StringProducerData {
    private String topic;

    /** the key used by the partitioner to pick a broker partition */
    private String key;

    /** variable length data to be published as Jafka messages under topic */
    private List<String> data;

    public StringProducerData() {

    }

    public StringProducerData(String topic, String key, List<String> data) {
        this.topic = topic;
        this.key = key;
        this.data = data;
    }

    public StringProducerData(String topic, List<String> data) {
        this(topic, null, data);
    }

    public StringProducerData(String topic, String data) {
        this.topic = topic;
        getData().add(data);
    }

    public StringProducerData(String topic) {
        this.topic = topic;
    }

    public List<String> getData() {
        if (data == null) {
            data = new ArrayList<String>();
        }
        return data;
    }

    public StringProducerData add(String message) {
        getData().add(message);
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

}
