package test.producer;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.PropertyConfigurator;

import com.google.common.util.concurrent.RateLimiter;
import com.takin.mq.message.StringProducerData;
import com.takin.mq.producer.ProducerService;
import com.takin.rpc.client.ProxyFactory;

public class ProducerTest {

    private static final RateLimiter limit = RateLimiter.create(100d);

    private static final AtomicInteger total = new AtomicInteger(0);

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            final ProducerService producer = ProxyFactory.create(ProducerService.class, "broker", null, null);
            //            for (int i = 0; i < 1; i++) {
            //                producer.send(new StringProducerData("demo").add("Hello jafka"));
            //            }
            //            for (int i = 0; i < 1; i++) {
            //                producer.send(new StringProducerData("demo").add("Hello jafka"));
            //            }

            while (true) {
                if (limit.tryAcquire()) {
                    long address = producer.send(new StringProducerData("test").add("Hello jafka" + total.getAndIncrement()));
                    System.out.println(address);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
