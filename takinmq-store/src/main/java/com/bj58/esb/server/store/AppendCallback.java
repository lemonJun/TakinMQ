package com.bj58.esb.server.store;

/**
 * Append回调
 * 
 * 
 */
public interface AppendCallback {

    /**
     * 在append成功后回调此方法，传入写入的location
     * 
     * @param location
     */
    public void appendComplete(Location location);
}