package com.bj58.esb.server.store.utils;

public interface MetaConfigMBean {
    /**
     * Reload topics configuration
     */
    public void reload();


    /** 关闭分区 */
    public void closePartitions(String topic, int start, int end);


    /** 打开一个topic的所有分区 */
    public void openPartitions(String topic);

}