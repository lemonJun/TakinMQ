package com.bj58.spat.esbclient.util;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.RejectedExecutionHandler;

public class DiscardOldestAndLogPolicy implements RejectedExecutionHandler {

    public DiscardOldestAndLogPolicy() {
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (!executor.isShutdown()) {
            try {
                executor.getQueue().size();
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
    }
}