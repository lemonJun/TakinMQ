package io.jafka.broker;

import com.takin.rpc.server.RPCServer;

public class BrokerStart {

    public static void main(String[] args) {
        try {
            new RPCServer().init(new String[] {}, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
