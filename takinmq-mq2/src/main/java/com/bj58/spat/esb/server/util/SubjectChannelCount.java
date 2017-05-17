package com.bj58.spat.esb.server.util;

import java.util.concurrent.atomic.AtomicInteger;

public class SubjectChannelCount {
	final AtomicInteger count = new AtomicInteger();

	public int getCount(int num) {
		return Math.abs(count.getAndIncrement() % num);
	}
}
