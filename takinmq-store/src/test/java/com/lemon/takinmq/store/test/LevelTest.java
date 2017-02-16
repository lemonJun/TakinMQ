package com.lemon.takinmq.store.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBComparator;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

public class LevelTest {

    private static final DB db = init();

    public static void main(String[] args) {
        try {
            
            Map map  = new HashMap<>();
            LevelTest test = new LevelTest();
            //            test.add();
            test.read();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                db.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void read() {

        DBIterator iterator = db.iterator();
        try {
            //            System.out.println(Iq80DBFactory.asString(db.get(Iq80DBFactory.bytes("1"))));
            System.out.println(Iq80DBFactory.asString(db.getfirst()));
            //            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
            //                String key = Iq80DBFactory.factory.asString(iterator.peekNext().getKey());
            //                String value = Iq80DBFactory.factory.asString(iterator.peekNext().getValue());
            //                System.out.println(key + " = " + value);
            //            }
        } finally {
            try {
                iterator.close();
            } catch (Exception e2) {
            }
        }
    }

    private void add() {
        try {
            for (int i = 5; i < 8; i++) {
                db.put(Iq80DBFactory.bytes(i + ""), Iq80DBFactory.bytes(i * i + ""));
            }
        } catch (DBException e) {
            e.printStackTrace();
        } finally {
            try {
            } catch (Exception e2) {
            }
        }
    }

    private static DB init() {
        try {
            //按正序排
            DBComparator comparator = new DBComparator() {
                public int compare(byte[] key1, byte[] key2) {
                    int v1 = Integer.parseInt(new String(key1));
                    int v2 = Integer.parseInt(new String(key2));
                    if (v1 > v2) {
                        return 1;
                    } else if (v1 == v2) {
                        return 0;
                    } else {
                        return -1;
                    }
                }

                public String name() {
                    return "simple";
                }

                public byte[] findShortestSeparator(byte[] start, byte[] limit) {
                    return start;
                }

                public byte[] findShortSuccessor(byte[] key) {
                    return key;
                }
            };
            Options options = new Options();
            options.comparator(comparator);
            options.createIfMissing(true);
            DB db = Iq80DBFactory.factory.open(new File("simple"), options);
            return db;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
