package com.bj58.esb.server.store;

/**
 * 分区内各个文件的信息
 *
 * 
 */
public class SegmentInfo {
    public final long startOffset;
    public final long size;


    public SegmentInfo(final long startOffset, final long size) {
        super();
        this.startOffset = startOffset;
        this.size = size;
    }

}