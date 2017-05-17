
package com.takin.mq.message;

public class StringProducerData {
    private String topic;

    private String key;

    private String data;

    public StringProducerData() {

    }

    public StringProducerData(String topic, String key, String data) {
        this.topic = topic;
        this.key = key;
        this.data = data;
    }

    public StringProducerData(String topic) {
        this.topic = topic;
    }

    public StringProducerData add(String message) {
        setData(message);
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
