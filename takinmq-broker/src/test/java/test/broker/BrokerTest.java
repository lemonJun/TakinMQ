package test.broker;

import com.takin.mq.broker.BrokerBootstrap;

public class BrokerTest {

    public static void main(String[] args) {
        BrokerBootstrap broker = new BrokerBootstrap();
        broker.init(new String[] {}, false);
        broker.startAsync().awaitRunning();
    }
}
