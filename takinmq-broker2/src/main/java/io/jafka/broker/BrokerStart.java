package io.jafka.broker;

import java.io.File;

import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.RPCServer;

public class BrokerStart {

    private static final RPCServer server = new RPCServer();

    public static void main(String[] args) {
        try {
            server.init(new String[] {}, false);
            server.start();
            initBroker();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void initBroker() {
        BrokerConfig config = GuiceDI.getInstance(BrokerConfig.class);
        config.init(server.getContext().getConfigPath() + File.separator + "broker.properties");
    }

}
