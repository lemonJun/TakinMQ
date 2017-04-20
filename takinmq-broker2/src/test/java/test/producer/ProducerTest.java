package test.producer;

import org.apache.log4j.PropertyConfigurator;

import com.takin.rpc.client.ProxyFactory;

import io.jafka.broker.ProducerService;
import io.jafka.broker.StringProducerData;

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
