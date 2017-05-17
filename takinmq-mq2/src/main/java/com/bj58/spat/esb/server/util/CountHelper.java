package com.bj58.spat.esb.server.util;

import java.util.concurrent.ConcurrentHashMap;

public class CountHelper {

    private final ConcurrentHashMap<String, SubjectChannelCount> map = new ConcurrentHashMap<String, SubjectChannelCount>();

    public SubjectChannelCount get(String key) {
        SubjectChannelCount v = map.get(key);
        if (v != null) {
            return v;
        } else {
            return map.put(key, new SubjectChannelCount());
        }
    }

    public SubjectChannelCount set(String key, SubjectChannelCount value) {
        return map.put(key, value);
    }

    private static class CountHelperHolder {
        public static CountHelper counthelper = new CountHelper();
    }

    public static CountHelper getCountHelper() {
        return CountHelperHolder.counthelper;
    }
}
