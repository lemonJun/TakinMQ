package com.takin.mq.message;

public class SimpleFetchData {

    private String topic;
    private int partition;
    private long startoffset;
    private long endoffset;
    private String data;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public long getStartoffset() {
        return startoffset;
    }

    public void setStartoffset(long startoffset) {
        this.startoffset = startoffset;
    }

    public long getEndoffset() {
        return endoffset;
    }

    public void setEndoffset(long endoffset) {
        this.endoffset = endoffset;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
