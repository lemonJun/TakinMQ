package com.takin.mq.client;

import com.takin.mq.message.SimpleFetchData;

public abstract class ReceiveHandler {

    public void notify(SimpleFetchData fetchdata) {
        messageReceived(fetchdata.getData());
    }

    public abstract void messageReceived(String msg);

}
