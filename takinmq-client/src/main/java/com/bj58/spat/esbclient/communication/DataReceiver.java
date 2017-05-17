package com.bj58.spat.esbclient.communication;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class DataReceiver {

    private final static Object locker = new Object();
    private static DataReceiver receiver = null;
    private NIOWorker worker = null;

    private DataReceiver() throws IOException {
        worker = new NIOWorker();
        Thread thread = new Thread(worker);
        thread.setName("ESBClient DataReceiver");
        thread.setDaemon(true);
        thread.start();
    }

    public static DataReceiver getInstance() throws ClosedChannelException, IOException {
        if (receiver == null) {
            synchronized (locker) {
                if (receiver == null) {
                    receiver = new DataReceiver();
                }
            }
        }

        return receiver;
    }

    public void regChannel(NIOChannel nioChannel) throws ClosedChannelException, IOException {
        synchronized (locker) {
            worker.register(nioChannel);
        }
    }
}

class NIOWorker implements Runnable {

    private static final Log logger = LogFactory.getLog(NIOWorker.class);
    private Object locker = new Object();
    private Selector selector;

    private List<NIOChannel> regList = new ArrayList<NIOChannel>();

    public NIOWorker() throws IOException {
        selector = Selector.open();
    }

    public void register(NIOChannel nioChannel) throws IOException {
        if (nioChannel.getSockChannel().isConnected()) {
            synchronized (locker) {
                regList.add(nioChannel);
            }
            selector.wakeup();
        } else {
            throw new IOException("channel is not open when register selector");
        }
    }

    @Override
    public void run() {
        while (true) {
            NIOChannel nioChannel = null;
            try {
                selector.select();
                if (regList.size() > 0) {
                    synchronized (locker) {
                        for (NIOChannel channel : regList) {
                            channel.getSockChannel().register(selector, SelectionKey.OP_READ, channel);
                        }
                        regList.clear();
                    }
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    if (key.isValid()) {
                        if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                            nioChannel = (NIOChannel) key.attachment();
                            nioChannel.frameHandle();
                        }
                    }
                }
                selectedKeys.clear();
            } catch (IOException e) {
                if (nioChannel != null) {
                    nioChannel.getServer().destroyChannel(nioChannel);
                    nioChannel.getServer().check();
                }
                logger.error("receive data error", e);
            } catch (NotYetConnectedException e) {
                if (nioChannel != null) {
                    nioChannel.getServer().destroyChannel(nioChannel);
                    nioChannel.getServer().check();
                }
                logger.error("receive data error", e);
            } catch (InterruptedException e) {
                logger.error("receive data error", e);
            } catch (Throwable t) {
                logger.error("receive data error", t);
            }
        }
    }

}
