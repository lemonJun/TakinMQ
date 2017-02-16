package com.lemon.takinmq.store.test;

import java.util.concurrent.ConcurrentSkipListMap;

public class SkipListMapTest {

    public static void main(String[] args) {
        ConcurrentSkipListMap<Integer, String> table = new ConcurrentSkipListMap<Integer, String>();
        for (int i = 1; i < 2; i++) {
            table.put(i, i * i + "");
        }

        System.out.println(table.firstKey());
    }
}
