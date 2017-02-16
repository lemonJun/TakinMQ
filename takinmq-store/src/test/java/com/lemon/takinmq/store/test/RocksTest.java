package com.lemon.takinmq.store.test;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class RocksTest {

    private RocksDB init() {
        Options options = new Options().setCreateIfMissing(true);
        RocksDB db = null;
        try {
            db = RocksDB.open(options, "rocks");
            return db;
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void add() {
        for (int i = 1; i < 10; i++) {
            try {
                db.put((i + "").getBytes(), (i * i + "").getBytes());
            } catch (RocksDBException e) {
                e.printStackTrace();
            }
        }
    }

    private final RocksDB db = init();

    public static void main(String[] args) {
        new RocksTest().init();
    }

}
