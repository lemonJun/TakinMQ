package test.broker;

import org.apache.log4j.PropertyConfigurator;

import com.takin.mq.delay.DelayMessageService;
import com.takin.rpc.server.GuiceDI;

public class MapDbTest {
    
    static {
        PropertyConfigurator.configure("D:/log4j.properties");
        GuiceDI.init();
    }

    public static void main(String[] args) {
        try {
            GuiceDI.getInstance(DelayMessageService.class).put(System.currentTimeMillis() + 30, System.currentTimeMillis() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
