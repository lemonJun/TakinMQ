package com.takin.mq.broker;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import com.google.common.util.concurrent.AbstractService;
import com.takin.mq.store2.LogManager;
import com.takin.mq.store2.RollingStrategy;
import com.takin.mq.utils.Scheduler;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.RPCServer;

public class BrokerBootstrap extends AbstractService {
    private static final RPCServer server = new RPCServer();

    private LogManager logManager;
    private BrokerConfig config;

    public void init(String[] args, boolean online) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            server.init(new String[] {}, false);
            initBroker();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

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

    public void dostop() {
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

    @Override
    protected void doStart() {
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    protected void doStop() {

    }
}
