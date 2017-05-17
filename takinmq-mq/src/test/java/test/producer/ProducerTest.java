package test.producer;

import org.apache.log4j.PropertyConfigurator;

import com.takin.mq.message.StringProducerData;
import com.takin.mq.producer.ProducerService;
import com.takin.rpc.client.ProxyFactory;

public class ProducerTest {

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            final ProducerService producer = ProxyFactory.create(ProducerService.class, "broker", null, null);
            //            for (int i = 0; i < 1; i++) {
            //                producer.send(new StringProducerData("demo").add("Hello jafka"));
            //            }
            for (int i = 0; i < 1; i++) {
                producer.send(new StringProducerData("demo").add("Hello jafka"), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
