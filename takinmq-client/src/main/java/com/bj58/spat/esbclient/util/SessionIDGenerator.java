package com.bj58.spat.esbclient.util;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SessionIDGenerator {

    private static final AtomicLong sessionID = new AtomicLong();
    private static long INFOID_FLAG = 1260000000000L;
    private static final Lock infoLock = new ReentrantLock();

    //	public static long getSessionID() {
    //		return sessionID.getAndIncrement();
    //	}

    public static long getSessionID() {
        infoLock.lock();
        try {
            long infoid = System.nanoTime() - INFOID_FLAG;
            infoid = (infoid << 7) | sessionID.getAndIncrement();
            return infoid;
        } finally {
            infoLock.unlock();
        }

    }
}
