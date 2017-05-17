package com.bj58.spat.esbclient.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import org.apache.commons.logging.Log;

import com.bj58.spat.esbclient.ESBClient;
import com.bj58.spat.esbclient.ESBMessage;
import org.apache.commons.logging.LogFactory;
import java.util.concurrent.locks.ReentrantLock;

import com.bj58.spat.esbclient.config.ESBConfig;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.bj58.spat.esbclient.config.ServerConfig;
import com.bj58.spat.esbclient.exception.CommunicationException;
import com.bj58.spat.esbclient.exception.ConnectTimeoutException;
import com.bj58.spat.esbclient.exception.SerializeException;
import com.bj58.spat.esbclient.util.LinkedTransferQueue;
import com.bj58.spat.esbclient.util.TransferQueue;

public class ServerPool {

    private static final Log logger = LogFactory.getLog(ServerPool.class);

    private AtomicInteger rr = new AtomicInteger(0);
    private final ReentrantLock lock = new ReentrantLock();
    private final ServerReciveHandler serverrecivehandler;
    private final List<Server> servList = new ArrayList<Server>();

    final TransferQueue<ESBMessage> messageQueue = new LinkedTransferQueue<ESBMessage>();

    final TransferQueue<ESBMessage> qmessageQueue = new LinkedTransferQueue<ESBMessage>();

    private final Thread sendtask = new Thread(new SendTask());

    private final Thread qsendtask = new Thread(new QueueSendTask());

    public ServerPool(ESBConfig esbConf, ESBClient client) throws IOException {
        serverrecivehandler = new ServerReciveHandler(esbConf);

        for (ServerConfig servConf : esbConf.getServConfList()) {
            try {
                Server serv = new Server(servConf, serverrecivehandler);
                servList.add(serv);
            } catch (IOException ex) {
                logger.error("create server error in ServerPool", ex);
            }
        }

        if (servList.size() == 0) {
            throw new IOException("can't connect to server serverPool size:0");
        }

        sendtask.setDaemon(true);
        qsendtask.setDaemon(true);
        sendtask.start();
        qsendtask.start();
    }

    public ServerReciveHandler getServerReciveHandler() {
        return serverrecivehandler;
    }

    private Server getServer() {
        if (servList.size() == 0) {
            logger.error("ServerPool is empty!");
            return null;
        }

        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (rr.get() > 10000) {
                rr.set(0);
            }
            int idx = rr.get() % servList.size();
            Server server = servList.get(idx);
            rr.getAndIncrement();
            if (server.getState() != ServerState.Normal) {
                Iterator<Server> iter = servList.iterator();
                while (iter.hasNext()) {
                    Server s = iter.next();
                    if (s.getState() == ServerState.Normal) {
                        return s;
                    }
                }
            }
            return server;
        } finally {
            lock.unlock();
        }
    }

    static Server currentSoleServer = null;

    private Server getSoleServer() {
        if (currentSoleServer == null || currentSoleServer.getState() != ServerState.Normal) {
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                for (Server item : servList) {
                    if (item.getState() == ServerState.Normal) {
                        currentSoleServer = item;
                        break;
                    }
                }

            } finally {
                lock.unlock();
            }
        }
        return currentSoleServer;
    }

    public Server getServer(boolean bool) {
        if (bool) {
            return this.getSoleServer();
        } else {
            return this.getServer();
        }
    }

    public List<Server> getAllServer() {
        return servList;
    }

    public void close() {
        Iterator<Server> iter = servList.iterator();
        while (iter.hasNext()) {
            Server s = iter.next();
            s.close();
        }
    }

    public void send(ESBMessage msg) {
        messageQueue.offer(msg);
    }

    /**
     * 发送消息(顺序性)
     * @param msg
     */
    public void sendQueue(ESBMessage msg) {
        qmessageQueue.offer(msg);
    }

    class SendTask implements Runnable {

        @Override
        public void run() {
            while (true) {
                ESBMessage message = null;
                try {
                    message = messageQueue.poll(999, TimeUnit.MILLISECONDS);
                    if (message != null) {
                        Server server = getServer();
                        try {
                            if (server == null) {
                                throw new Exception("cannot get server,this server is dead.");
                            }
                            server.sendMessage(message);
                        } catch (IOException e) {
                            logger.error("send message error", e);
                        } catch (ConnectTimeoutException e) {
                            logger.error("send message error", e);
                        } catch (SerializeException e) {
                            e.printStackTrace();
                        } catch (CommunicationException e) {
                            e.printStackTrace();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    class QueueSendTask implements Runnable {

        @Override
        public void run() {
            while (true) {
                ESBMessage message = null;
                try {
                    message = qmessageQueue.poll(999, TimeUnit.MILLISECONDS);
                    if (message != null) {
                        Server server = getSoleServer();
                        try {
                            if (server == null) {
                                throw new Exception("cannot get server,this server is dead.");
                            }
                            server.sendQueueMessage(message);
                        } catch (IOException e) {
                            logger.error("send message error", e);
                        } catch (ConnectTimeoutException e) {
                            logger.error("send message error", e);
                        } catch (SerializeException e) {
                            e.printStackTrace();
                        } catch (CommunicationException e) {
                            e.printStackTrace();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
