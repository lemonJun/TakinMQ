package test;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.PropertyConfigurator;

import com.google.common.util.concurrent.RateLimiter;
import com.takin.mq.client.ProducerProvider;
import com.takin.mq.message.SimpleSendData;
import com.takin.mq.producer.ProducerService;

public class ProducerDealyTest {

    private static final RateLimiter limit = RateLimiter.create(1d);

    private static final AtomicInteger total = new AtomicInteger(0);

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            final ProducerService producer = ProducerProvider.getProducerByTopic();
            while (true) {
                if (limit.tryAcquire()) {
                    SimpleSendData data = new SimpleSendData("delay").add("" + total.getAndIncrement()).delay(30l);
                    long address = producer.sendDelayMsg(data);
                    System.out.println("offset: " + address);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
