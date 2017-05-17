package com.bj58.spat.esbclient;

import java.util.concurrent.ExecutorService;

public abstract class ESBReceiveHandler {

    public void notify(final byte[] msgBuf, ExecutorService executor) {
        ESBMessage msg = ESBMessage.fromBytes(msgBuf);
        messageReceived(msg);
    }

    public abstract void messageReceived(ESBMessage msg);
    
}
