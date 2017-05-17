package com.bj58.spat.esb.server.daemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.bj58.spat.esb.server.config.Client;
import com.bj58.spat.esb.server.config.SubjectFactory;
import com.bj58.spat.esb.server.jsr166.LinkedTransferQueue;
import com.bj58.spat.esb.server.jsr166.TransferQueue;
import com.bj58.spat.esb.server.protocol.ESBMessage;
import com.bj58.spat.esb.server.store.EsbMessageDao;

public class ErrorWorker {

    private static final Log logger = LogFactory.getLog(ErrorWorker.class);
    private final static int T_COUNT = 1;
    private static ErrorWorker instrance = new ErrorWorker();
    /**发送失败消息队列*/
    private static final TransferQueue<ESBMessage> errorQueue = new LinkedTransferQueue<ESBMessage>();
    private EsbMessageDao msgStore = new EsbMessageDao();;
    private Thread storeWorker[] = new Thread[T_COUNT];

    public static synchronized ErrorWorker getInstrance() {
        return instrance;
    }

    public void offer(ESBMessage msg) {
        if (errorQueue.size() > 30000) {
            logger.warn("ErrorWorker size > 30000");
        }
        errorQueue.offer(msg);
    }

    /**
     * 
     * @return 发送时失败的队列的大小
     */
    public static int getErrorQSize() {
        return errorQueue.size();
    }

    private ErrorWorker() {
        for (int i = 0; i < T_COUNT; i++) {
            storeWorker[i] = new Thread(new Runnable() {
                public void run() {
                    List<ESBMessage> msgList = new ArrayList<ESBMessage>(50);
                    while (true) {
                        try {
                            ESBMessage msg = errorQueue.poll(1000, TimeUnit.MILLISECONDS);
                            if (msg != null) {
                                msgList.add(msg);
                                if (msgList.size() == 50) {
                                    try {
                                        msgStore.insertMessage(msgList);
                                    } finally {
                                        msgList.clear();
                                    }
                                    logger.debug("insert into message and pool size is " + errorQueue.size());
                                }
                            } else {
                                if (msgList.size() > 0) {
                                    try {
                                        logger.error("msgList size is :" + msgList.size());
                                        msgStore.insertMessage(msgList);
                                    } finally {
                                        msgList.clear();
                                    }
                                }
                            }
                            msg = null;
                        } catch (Exception ex) {
                            logger.error("ErrorWorker insertThread write error", ex);
                        }
                    }
                }
            });
            storeWorker[i].setName("ErrorWorker insertThread[" + i + "]");
            storeWorker[i].setDaemon(true);
        }

    }

    public void start() {
        for (int i = 0; i < T_COUNT; i++) {
            storeWorker[i].start();
        }
    }

    volatile static Map<String, Object> hasSenderFlag = new ConcurrentHashMap<String, Object>();

    class SendWorker extends Thread {

        private int subject;
        private int clientId;

        public SendWorker(int subject, int clientId) {
            this.subject = subject;
            this.clientId = clientId;
            this.setName("DaemonWorker write thread for subject :" + subject);
        }

        @Override
        public void run() {
            synchronized (SendWorker.class) {
                if (hasSenderFlag.get(subject + "_" + clientId) != null) {
                    return;
                } else {
                    hasSenderFlag.put(subject + "_" + clientId, new Object());
                }
            }
            List<ESBMessage> msgList = null;
            try {
                logger.info("a sender worker begin works------------------------ ");
                Thread.sleep(1000);
                outer: for (msgList = msgStore.readMessage(100, subject, clientId); msgList != null && msgList.size() > 0; msgList = msgStore.readMessage(100, subject, clientId)) {
                    logger.info("queried msglist size------------------------ :" + msgList.size());
                    for (int i = 0; i < msgList.size(); i++) {
                        ESBMessage msg = msgList.get(i);
                        if (msg == null) {
                            continue;
                        }
                        msg.setCommandType((byte) 2);
                        if (rePublish(msg)) {
                            //msgStore.deleteMessage(msg);
                        } else {
                            List<ESBMessage> sublist = msgList.subList(i, msgList.size());
                            int length = 4 * sublist.size();
                            for (ESBMessage m : sublist) {
                                length += m.toBytes().length;
                            }
                            msgStore.goBack(length, subject, clientId);
                            logger.debug("message resend faild will break");
                            break outer;
                        }
                    }

                    if (msgList.size() < 100) {
                        break outer;
                    }
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                logger.error("DaemonWorker writeThread read error for subject ：" + subject, e);
            } finally {
                msgList.clear();
                msgList = null;
            }
            hasSenderFlag.remove(subject + "_" + clientId);
        }

    }

    /**
     * rePush message
     * DB拉取数据重发
     * @param msg
     * @throws CloneNotSupportedException
     */
    private boolean rePublish(ESBMessage msg) throws CloneNotSupportedException {
        Client tClient = null;
        List<Client> clientList = SubjectFactory.getSubjectFactory().getClientList(msg.getSubject());
        if (clientList != null) {
            for (Client client : clientList) {
                if (msg.getClientID() == client.getClientID()) {
                    tClient = client;
                    break;
                }
            }
        } else {
            logger.info("this subject[" + msg.getSubject() + "] not hava clientID!");
        }

        if (tClient == null) {
            return false;
        }
        return MessageHandle.publishRe(tClient, msg);
    }
}
