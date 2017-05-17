package com.bj58.spat.esbclient.communication;

import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChannelPool {

    private static final Log logger = LogFactory.getLog(ChannelPool.class);

    final ReentrantLock lock = new ReentrantLock();
    private final List<NIOChannel> channels = new CopyOnWriteArrayList<NIOChannel>();

    public NIOChannel getChannel() throws Exception {
        if (channels.size() == 0) {
            throw new IOException("NioChannelPool is empty");
        }
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (channels.size() == 0) {
                throw new Exception("NioChannelPool is empty");
            }
            int idx = (int) (System.nanoTime() % channels.size());
            return channels.get(idx);
        } finally {
            lock.unlock();
        }
    }

    public List<NIOChannel> getAllChannel() {
        return channels;
    }

    public void destroy(NIOChannel channel) {
        logger.warn("socket destroyed!--" + channel.toString());

        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (channel.isOpen()) {
                channel.close();
            }
            channels.remove(channel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void destroy() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Iterator<NIOChannel> iter = channels.iterator();
            while (iter.hasNext()) {
                NIOChannel channel = iter.next();
                if (channel.isOpen()) {
                    channel.close();
                }
                channels.remove(channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public int count() {
        return channels.size();
    }

    public void add(NIOChannel channel) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            channels.add(channel);
        } finally {
            lock.unlock();
        }
    }
}
