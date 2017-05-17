package com.bj58.esb.server.store.utils;

import java.util.zip.CRC32;


/**
 * Checksum计算器
 * 
 * 
 */
public class CheckSum {
    public static final int crc32(byte[] array) {
        return crc32(array, 0, array.length);
    }


    public static final int crc32(byte[] array, int offset, int length) {
        CRC32 crc32 = new CRC32();
        crc32.update(array, offset, length);
        return (int) (crc32.getValue() & 0x7FFFFFFF);
    }
}