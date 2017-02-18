package com.lemon.takinmq.store.test.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.iq80.leveldb.impl.FileChannelLogWriter;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.iq80.leveldb.impl.LogMonitor;
import org.iq80.leveldb.impl.LogReader;
import org.iq80.leveldb.impl.LogWriter;
import org.iq80.leveldb.util.Slice;

public class SliceWrite {

    private static final Random r = new Random();

    private static final AtomicLong readcnt = new AtomicLong(0);

    private static void init() {
        try {
            LogWriter writer = new FileChannelLogWriter(new File("D:/sfile/slice2"), 1, false);
            for (int i = 1; i < 3100; i++) {
                Slice s = new Slice(String.valueOf(i).getBytes());
                writer.addRecord(s, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void read() {
        try {
            LogReader read = new LogReader(new FileInputStream(new File("D:/sfile/slice2")).getChannel(), throwExceptionMonitor(), true, 32790);
            for (Slice record = read.readRecord(); record != null; record = read.readRecord()) {
                System.out.println("->" + readcnt.addAndGet(record.length() + 7));
                System.out.println(Iq80DBFactory.asString(record.getBytes()));
            }
            //            System.out.println(Iq80DBFactory.asString(read.readRecord().getBytes()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //        init();
        read();
    }

    public static LogMonitor throwExceptionMonitor() {
        return new LogMonitor() {
            @Override
            public void corruption(long bytes, String reason) {
                throw new RuntimeException(String.format("corruption of %s bytes: %s", bytes, reason));
            }

            @Override
            public void corruption(long bytes, Throwable reason) {
                throw new RuntimeException(String.format("corruption of %s bytes", bytes), reason);
            }
        };
    }

}
