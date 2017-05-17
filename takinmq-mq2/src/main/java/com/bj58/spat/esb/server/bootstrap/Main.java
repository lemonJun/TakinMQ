package com.bj58.spat.esb.server.bootstrap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.bj58.spat.esb.server.communication.TcpServer;
import com.bj58.spat.esb.server.communication.telnet.TelnetServer;
import com.bj58.spat.esb.server.config.ServiceConfig;
import com.bj58.spat.esb.server.config.SubConfigFactory;
import com.bj58.spat.esb.server.store.AbstractDao;

public class Main {

    private static final Log logger = LogFactory.getLog(Main.class);

    public static void main(String[] args) throws Exception {
        String userDir = System.getProperty("user.dir");
        String rootPath = userDir + "/../conf/";

        String esbConfigPath = rootPath + "esb_config.xml";
        String sujectConfig = rootPath + "sub_config.xml";
        /**load server 配置*/
        ServiceConfig.getServiceConfig(esbConfigPath);
        SubConfigFactory.load(sujectConfig);

        TcpServer.getInstance().start();
        TelnetServer.getInstance().start();

        logger.info("ESB Server starting...");

        try {
            registerExcetEven();
        } catch (Exception e) {
            logger.error("registerExcetEven error", e);
            System.exit(0);
        }

        logger.info("ESB Server start success");

        while (true) {
            Thread.sleep(1000 * 60 * 60);
        }
    }

    /**
     * when shutdown server destroyed all socket connection
     */
    private static void registerExcetEven() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    TcpServer.getInstance().stop();
                    TelnetServer.getInstance().stop();
                    AbstractDao.flushAllAndClose();
                } catch (Exception e) {
                    logger.error("Stop Server Error", e);
                }

                try {
                    super.finalize();
                } catch (Throwable e) {
                    logger.error("super.finalize() error when stop server", e);
                }
            }
        });
    }

}
