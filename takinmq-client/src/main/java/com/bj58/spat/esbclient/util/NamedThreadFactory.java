package com.bj58.spat.esbclient.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private final String name;
    private boolean isDaemon = false;
    private final AtomicInteger threadNum = new AtomicInteger(0);

    public NamedThreadFactory(String name) {
        this.name = name;
    }

    public NamedThreadFactory(String name, boolean isDaemon) {
        this.name = name;
        this.isDaemon = isDaemon;
    }

    @Override
    public synchronized Thread newThread(Runnable r) {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setName(name + "-" + threadNum.getAndIncrement());
        t.setDaemon(isDaemon);
        return t;
    }
}
