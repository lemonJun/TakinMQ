package com.lemon.takinmq.naming;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.testng.internal.thread.ThreadUtil.ThreadFactoryImpl;

import com.lemon.takinmq.common.ImoduleService;
import com.lemon.takinmq.naming.routeinfo.RouteInfoManager;

/**
 * 启动服务服务注册模块
 *
 * @author WangYazhou
 * @date  2017年2月7日 下午2:10:36
 * @see
 */
public class NamingStartUp implements ImoduleService {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl("NSScheduledThread"));

    private final RouteInfoManager routeInfoManager;

    public NamingStartUp() {
        this.routeInfoManager = new RouteInfoManager();
    }

    @Override
    public void init() throws Exception {
        
        
        
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                NamingStartUp.this.routeInfoManager.scanNotActiveBroker();
            }
        }, 5, 10, TimeUnit.SECONDS);
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void info() throws Exception {

    }

}
