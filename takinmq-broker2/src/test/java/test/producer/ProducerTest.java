package test.producer;

import com.takin.rpc.client.ProxyFactory;

import io.jafka.broker.ProducerService;
import io.jafka.broker.StringProducerData;

public class ProducerTest {

    public static void main(String[] args) {
        try {
            final ProducerService producer = ProxyFactory.create(ProducerService.class, "test", null, null);
            producer.send(new StringProducerData("demo").add("Hello jafka").add("https://github.com/adyliu/jafka"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
