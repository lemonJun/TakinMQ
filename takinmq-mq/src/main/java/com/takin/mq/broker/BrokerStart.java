package com.takin.mq.broker;

import java.io.File;

import com.takin.mq.log.LogManager;
import com.takin.mq.log.RollingStrategy;
import com.takin.mq.utils.Scheduler;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.RPCServer;

public class BrokerStart {

    private static final RPCServer server = new RPCServer();

    public static void main(String[] args) {
        try {
            server.init(new String[] {}, false);
            server.start();
            new BrokerStart().initBroker();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private LogManager logManager;
    private BrokerConfig config;

    public void initBroker() throws Exception {
        config = GuiceDI.getInstance(BrokerConfig.class);
        config.init(server.getContext().getConfigPath() + File.separator + "broker.properties");
        //        
        final Scheduler scheduler = new Scheduler(1, "jafka-logcleaner-", false);
        RollingStrategy rolling = null;
        this.logManager = GuiceDI.getInstance(LogManager.class);
        this.logManager.setRollingStategy(rolling);
        this.logManager.setScheduler(scheduler);
        this.logManager.setNeedRecovery(true);

        logManager.load();
        logManager.startup();
    }

    final String CLEAN_SHUTDOWN_FILE = ".jafka_cleanshutdown";

    public void close() {
        try {
            logManager.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            server.shutdown();
        } catch (Exception e) {
            e.printStackTrace();

        }
        try {
            File cleanShutDownFile = new File(new File(config.getLogdirs()), CLEAN_SHUTDOWN_FILE);
            cleanShutDownFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
