package com.bj58.spat.esb.server.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolService {
    public static final ExecutorService executor = Executors.newFixedThreadPool(30);
}
