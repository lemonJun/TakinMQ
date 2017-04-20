package test.producer;

import org.apache.log4j.PropertyConfigurator;

import com.takin.mq.broker.ProducerService;
import com.takin.mq.broker.StringProducerData;
import com.takin.rpc.client.ProxyFactory;

public class ProducerTest {

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            final ProducerService producer = ProxyFactory.create(ProducerService.class, "test", null, null);
            for (int i = 0; i < 2; i++) {
                producer.send(new StringProducerData("demo").add("Hello jafka").add("https://github.com/adyliu/jafka"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
