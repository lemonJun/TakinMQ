package com.lemon.takinmq.common;

public interface ImoduleService {

    public abstract void init() throws Exception;

    public abstract void start() throws Exception;

    public abstract void destroy() throws Exception;

}
