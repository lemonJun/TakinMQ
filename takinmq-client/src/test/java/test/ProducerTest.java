package test;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.PropertyConfigurator;

import com.google.common.util.concurrent.RateLimiter;
import com.takin.mq.client.ProducerProvider;
import com.takin.mq.message.SimpleSendData;
import com.takin.mq.producer.ProducerService;

public class ProducerTest {

    private static final RateLimiter limit = RateLimiter.create(3d);

    private static final AtomicInteger total = new AtomicInteger(0);

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            final ProducerService producer = ProducerProvider.getProducerByTopic();
            while (true) {
                if (limit.tryAcquire()) {
                    long address = producer.send(new SimpleSendData("test").add("Hello jafka" + total.getAndIncrement()), 2);
                    System.out.println(address);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
