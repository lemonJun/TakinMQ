package com.bj58.spat.esbclient.communication;

import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import java.util.concurrent.BlockingQueue;
import org.apache.commons.logging.LogFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.bj58.spat.esbclient.config.ESBConfig;
import com.bj58.spat.esbclient.util.NamedThreadFactory;
import com.bj58.spat.esbclient.util.DiscardOldestAndLogPolicy;

public class ServerReciveHandler {

    private static final Log logger = LogFactory.getLog(ServerReciveHandler.class);
    private ESBConfig esbConf;
    private final static ThreadPoolExecutor notifyExec = new ThreadPoolExecutor(1, 1, 1500L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("[ESB-Client] NotifyWorker", true), new DiscardOldestAndLogPolicy());

    public ServerReciveHandler(ESBConfig esbConf) {
        this.esbConf = esbConf;
    }

    AtomicInteger count = new AtomicInteger();

    public void offer(byte[] msg) {
        notifyExec.execute(new ReciveTask(msg, esbConf));
        if (notifyExec.getQueue().size() != 0 && notifyExec.getQueue().size() % 1000 == 0) {
            logger.warn("notifyQueue size is " + notifyExec.getQueue().size());
        }
    }

    public BlockingQueue<Runnable> getNotifyQueue() {
        return notifyExec.getQueue();
    }

    public void shutdown() {
        logger.warn("[ESB-Client] ServerReciveHandler will shutdown!");
        notifyExec.shutdown();
        while (!notifyExec.isTerminated()) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.warn("[ESB-Client] ServerReciveHandler is shutdown!");
    }
}

class ReciveTask implements Runnable {

    final byte[] msgBuff;
    final ESBConfig esbConf;

    public ReciveTask(byte[] msgBuff, ESBConfig esbConf) {
        this.msgBuff = msgBuff;
        this.esbConf = esbConf;
    }

    @Override
    public void run() {
        if (msgBuff != null && msgBuff.length != 0) {
            esbConf.getReceiveHandler().notify(msgBuff, esbConf.getExecutor());
        }
    }
}
