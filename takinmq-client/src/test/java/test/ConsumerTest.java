package test;

import org.apache.log4j.PropertyConfigurator;

import com.takin.mq.client.ConsumerProvider;
import com.takin.mq.client.ReceiveHandler;

public class ConsumerTest {
    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");

            ConsumerProvider.registTopicHandler("test", new ReceiveHandler() {
                @Override
                public void messageReceived(String msg) {
                    System.out.println(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
