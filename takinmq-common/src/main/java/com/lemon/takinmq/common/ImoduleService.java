package com.lemon.takinmq.common;

public interface ImoduleService {

    //初始化配置
    public abstract void env() throws Exception;

    //工程初始化
    public abstract void init() throws Exception;

    //启动
    public abstract void start() throws Exception;

    //销毁时释放资源
    public abstract void destroy() throws Exception;

}
