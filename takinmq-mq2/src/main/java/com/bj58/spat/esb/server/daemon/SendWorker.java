package com.bj58.spat.esb.server.daemon;

import java.util.concurrent.TimeUnit;
import com.bj58.spat.esb.server.config.Subject;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.bj58.spat.esb.server.util.MessageCounter;
import com.bj58.spat.esb.server.util.MessageHelp;
import com.bj58.spat.esb.server.util.SystemUtils;
import com.bj58.spat.esb.server.protocol.ESBMessage;
import com.bj58.spat.esb.server.config.SubjectFactory;
import com.bj58.spat.esb.server.util.ThreadRenameFactory;
import com.bj58.spat.esb.server.communication.ESBContext;

/**
 * push worker 
 * @author Service Platform Architecture Team (spat@58.com)
 */
public class SendWorker {

    private static final int COUNT = SystemUtils.getSystemThreadCount();
    private final static ThreadPoolExecutor exe = new ThreadPoolExecutor(COUNT, COUNT, 1500L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadRenameFactory("ESB-Send-Thread"));

    public void offer(final ESBContext ctx) {
        exe.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (ctx != null) {
                        ESBMessage msg = ESBMessage.fromBytes(ctx.getBuf());
                        MessageCounter.increase(new Integer(msg.getSubject()), MessageCounter.CounterType.IN);
                        if (msg != null) {
                            msg.setMessageID(MessageHelp.getMessageID());
                            msg.setCommandType((byte) 2);/**推送消息*/
                            /**判断当前主题是否为队列*/
                            Subject subject = SubjectFactory.getSubjectFactory().getSubjectMap().get(msg.getSubject());
                            if (subject == null) {
                                return;
                            }
                            if (subject.isQueue()) {
                                msg.setTimestamp(ctx.getInTime());
                                QueueInsertWorker.inQueueWorker(msg);
                            } else {
                                /**推送订阅者*/
                                MessageHandle.publish(msg);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static int getQSize() {
        return exe.getQueue().size();
    }

    public static void shutdown() {
        exe.shutdown();
    }
}
