# TakinMQ
 
## broker启动 
```
public class BrokerTest {

    public static void main(String[] args) {
        BrokerBootstrap broker = new BrokerBootstrap();
        broker.init(new String[] {}, false);
        broker.startAsync().awaitRunning();
    }
}
```

## producer启动 
```
public class ProducerTest {

    private static final RateLimiter limit = RateLimiter.create(5d);

    private static final AtomicInteger total = new AtomicInteger(0);

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            final ProducerService producer = ProducerProvider.getProducerByTopic();
            while (true) {
                if (limit.tryAcquire()) {
                    long address = producer.send(new SimpleSendData("delay").add("hello" + total.getAndIncrement()));
                    System.out.println("offset: " + address);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## consumer启动
```
public class ConsumerTest {
    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");

            ConsumerProvider.registTopicHandler("delay", new ReceiveHandler() {
                @Override
                public void messageReceived(String msg) {
                    //
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```


