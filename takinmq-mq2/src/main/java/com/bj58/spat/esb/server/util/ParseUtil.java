package com.bj58.spat.esb.server.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtil {

    public static int parseInt(Object obj, int defaultValue) {
        if (obj == null)
            return defaultValue;
        try {
            return Integer.valueOf(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static long parseLong(Object obj, long defaultValue) {
        if (obj == null)
            return defaultValue;
        try {
            return Long.valueOf(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static boolean parseBoolean(Object obj, boolean defaultValue) {
        if (obj == null)
            return defaultValue;
        try {
            return Boolean.valueOf(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static String parseString(Object obj, String defaultValue) {
        if (obj == null)
            return defaultValue;
        return obj.toString();
    }

    public static String fillStringByArgs(String str, String... arr) {
        Matcher m = Pattern.compile("\\{(\\d)\\}").matcher(str);
        while (m.find()) {
            str = str.replace(m.group(), arr[Integer.parseInt(m.group(1))]);
        }
        return str;
    }

}
