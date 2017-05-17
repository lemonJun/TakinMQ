package com.bj58.spat.esb.server.util;

public class ByteConverter {

    /**
     * byte array to short (little endian)
     * 
     * @param buf
     * @return
     */
    public static short bytesToShortLittleEndian(byte buf[]) {
        return (short) (((buf[0] << 8) & 0xff00) | (buf[1] & 0xff));
    }

    /**
     * byte array to int (little endian)
     * 
     * @param buf
     * @return
     */
    public static int bytesToIntLittleEndian(byte buf[]) {
        return ((buf[0] << 24) & 0xff000000) | ((buf[1] << 16) & 0xff0000) | ((buf[2] << 8) & 0xff00) | (buf[3] & 0xff);
    }

    /**
     * byte array to int (little endian)
     * 
     * @param buf
     * @return
     */
    public static long bytesToLongLittleEndian(byte buf[]) {
        return (((long) buf[0] << 56) & 0xff00000000000000l) | (((long) buf[1] << 48) & 0xff000000000000l) | (((long) buf[2] << 40) & 0xff0000000000l) | (((long) buf[3] << 32) & 0xff00000000l) | (((long) buf[4] << 24) & 0xff000000l) | (((long) buf[5] << 16) & 0xff0000l) | (((long) buf[6] << 8) & 0xff00l) | ((long) buf[7] & 0xffl);
    }

    /**
     * 
     * @param buf
     * @return
     */
    public static short bytesToShortBigEndian(byte[] buf) {
        return (short) (buf[0] & 0xff | ((buf[1] << 8) & 0xff00));
    }

    /**
     * 
     * @param buf
     * @return
     */
    public static int bytesToIntBigEndian(byte[] buf) {
        return buf[0] & 0xff | ((buf[1] << 8) & 0xff00) | ((buf[2] << 16) & 0xff0000) | ((buf[3] << 24) & 0xff000000);
    }

    /**
     * byte array to int (big endian)
     * 
     * @param buf
     * @return
     */
    public static long bytesToLongBigEndian(byte[] buf) {
        return (long) buf[0] & 0xffl | (((long) buf[1] << 8) & 0xff00l) | (((long) buf[2] << 16) & 0xff0000l) | (((long) buf[3] << 24) & 0xff000000l) | (((long) buf[4] << 32) & 0xff00000000l) | (((long) buf[5] << 40) & 0xff0000000000l) | (((long) buf[6] << 48) & 0xff000000000000l) | (((long) buf[7] << 56) & 0xff00000000000000l);
    }

    public static byte[] shortToBytesLittleEndian(short n) {
        byte[] buf = new byte[2];
        for (int i = 0; i < buf.length; i++) {
            buf[buf.length - i - 1] = (byte) (n >> (8 * i));
        }
        return buf;
    }

    /**
     * int to byte array (little endian)
     * 
     * @param n
     * @return
     */
    public static byte[] intToBytesLittleEndian(int n) {
        byte[] buf = new byte[4];
        for (int i = 0; i < buf.length; i++) {
            buf[buf.length - i - 1] = (byte) (n >> (8 * i));
        }
        return buf;
    }

    public static byte[] longToBytesLittleEndian(long n) {
        byte[] buf = new byte[8];
        for (int i = 0; i < buf.length; i++) {
            buf[buf.length - i - 1] = (byte) (n >> (8 * i));
        }
        return buf;
    }

    public static byte[] shortToBytesBigEndian(short n) {
        byte[] buf = new byte[2];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) (n >> (8 * i));
        }
        return buf;
    }

    /**
     * int to byte array (big endian)
     * 
     * @param n
     * @return
     */
    public static byte[] intToBytesBigEndian(int n) {
        byte[] buf = new byte[4];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) (n >> (8 * i));
        }
        return buf;
    }

    public static byte[] longToBytesBigEndian(long n) {
        byte[] buf = new byte[8];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) (n >> (8 * i));
        }
        return buf;
    }

    public static short bytesToShortLittleEndian(byte[] buf, int offset) {
        return (short) (((buf[offset] << 8) & 0xff00) | (buf[offset + 1] & 0xff));
    }

    public static short bytesToShortBigEndian(byte[] buf, int offset) {
        return (short) (buf[offset] & 0xff | ((buf[offset + 1] << 8) & 0xff00));
    }

    public static int bytesToIntLittleEndian(byte buf[], int offset) {
        return ((buf[offset] << 24) & 0xff000000) | ((buf[offset + 1] << 16) & 0xff0000) | ((buf[offset + 2] << 8) & 0xff00) | (buf[offset + 3] & 0xff);
    }

    public static int bytesToIntBigEndian(byte[] buf, int offset) {
        return buf[offset] & 0xff | ((buf[offset + 1] << 8) & 0xff00) | ((buf[offset + 2] << 16) & 0xff0000) | ((buf[offset + 3] << 24) & 0xff000000);
    }

    public static long bytesToLongLittleEndian(byte buf[], int offset) {
        return ((buf[offset] << 56) & 0xff00000000000000l) | ((buf[offset + 1] << 48) & 0xff000000000000l) | ((buf[offset + 2] << 40) & 0xff0000000000l) | ((buf[offset + 3] << 32) & 0xff00000000l) | ((buf[offset + 4] << 24) & 0xff000000l) | ((buf[offset + 5] << 16) & 0xff0000l) | ((buf[offset + 6] << 8) & 0xff00l) | (buf[offset + 7] & 0xffl);
    }

    public static long bytesToLongBigEndian(byte[] buf, int offset) {
        return (long) buf[offset] & 0xffl | (((long) buf[offset + 1] << 8) & 0xff00l) | (((long) buf[offset + 2] << 16) & 0xff0000l) | (((long) buf[offset + 3] << 24) & 0xff000000l) | (((long) buf[offset + 4] << 32) & 0xff00000000l) | (((long) buf[offset + 5] << 40) & 0xff0000000000l) | (((long) buf[offset + 6] << 48) & 0xff000000000000l) | (((long) buf[offset + 7] << 56) & 0xff00000000000000l);
    }
}