package com.bj58.spat.esbclient.communication.detect;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import com.bj58.spat.esbclient.ESBMessage;
import com.bj58.spat.esbclient.communication.Server;
import com.bj58.spat.esbclient.communication.ServerState;
import com.bj58.spat.esbclient.exception.ConnectTimeoutException;
import com.bj58.spat.esbclient.exception.SerializeException;

public class ServerStateDetect {

    private final ConcurrentHashMap<Integer, Server> map = new ConcurrentHashMap<Integer, Server>();
    private final ReentrantLock lock = new ReentrantLock();
    private static final ServerStateDetect serverstatedetect = new ServerStateDetect();

    public static ServerStateDetect getInstance() {
        return serverstatedetect;
    }

    public void check(Server server) {
        lock.lock();
        try {
            if (map.containsKey(server.hashCode())) {
                return;
            }
            server.setState(ServerState.Testing);
            map.put(server.hashCode(), server);
            new Thread(new DetectTimeJob(server)).start();
        } finally {
            lock.unlock();
        }
    }

    class DetectTimeJob implements Runnable {
        final Server server;

        public DetectTimeJob(Server server) {
            this.server = server;
        }

        @Override
        public void run() {
            DetectChannel detectchannel = null;
            try {
                detectchannel = new DetectChannel(server);
                ESBMessage message = new ESBMessage();
                message.setCommandType((byte) 5);
                detectchannel.send(message.toBytes());
                while (true) {
                    try {
                        int sagle = detectchannel.frameHandle();
                        if (sagle == 0) {
                            Thread.sleep(50);
                        }
                        if (sagle != 0) {
                            break;
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        sleep(3000);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                sleep(3000);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                sleep(3000);
            } catch (SerializeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != detectchannel) {
                        detectchannel.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    lock.lock();
                    try {
                        map.remove(server.hashCode());
                    } finally {
                        lock.unlock();
                    }
                    if (server.getState() != ServerState.Normal) {
                        check(server);
                    }
                }
            }
        }
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
