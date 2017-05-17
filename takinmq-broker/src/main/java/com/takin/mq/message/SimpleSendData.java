
package com.takin.mq.message;

public class SimpleSendData {

    private String topic;
    private String data;

    public SimpleSendData() {
    }

    public SimpleSendData(String topic, String data) {
        this.topic = topic;
        this.data = data;
    }

    public SimpleSendData(String topic) {
        this.topic = topic;
    }

    public SimpleSendData add(String message) {
        setData(message);
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
