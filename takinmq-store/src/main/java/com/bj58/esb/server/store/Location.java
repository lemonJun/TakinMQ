package com.bj58.esb.server.store;

/**
 * 数据存入的位置
 * 
 * 
 */
public class Location {
    protected final long offset;
    protected final int length;

    public static Location InvalidLocaltion = new Location(-1, -1);


    protected Location(final long offset, final int length) {
        super();
        this.offset = offset;
        this.length = length;
    }


    public static Location create(long offset, int length) {
        if (offset < 0 || length < 0) {
            return InvalidLocaltion;
        }
        return new Location(offset, length);
    }


    public boolean isValid() {
        return this != InvalidLocaltion;
    }


    public long getOffset() {
        return this.offset;
    }


    public int getLength() {
        return this.length;
    }

}