package test;

import org.apache.log4j.PropertyConfigurator;

import com.takin.mq.client.ConsumerProvider;
import com.takin.mq.client.ReceiveHandler;

public class ConsumerTest {
    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            //            final FetchService fetch = ProxyFactory.create(FetchService.class, "broker", null, null);
            //            //            for (int i = 0; i < 1; i++) {
            //            //                producer.send(new StringProducerData("demo").add("Hello jafka"));
            //            //            }
            //            SimpleFetchData data = fetch.fetch("test", 0L);
            //            System.out.println(JSON.toJSONString(data));
            //            

            ConsumerProvider.registTopicHandler("broker", new ReceiveHandler() {
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
