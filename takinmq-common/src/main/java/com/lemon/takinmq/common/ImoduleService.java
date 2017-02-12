package com.lemon.takinmq.common;

public interface ImoduleService {

    /**
     * 读取配置文件
     * 如查没有配置文件  则按默认配置的来
     */
    public abstract void loadconfig();

    //工程初始化
    public abstract void init() throws Exception;

    //启动
    public abstract void start() throws Exception;

    //销毁时释放资源
    public abstract void shutdown() throws Exception;

}
