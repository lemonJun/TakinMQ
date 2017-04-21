package test.consumer;

import org.apache.log4j.PropertyConfigurator;

import com.alibaba.fastjson.JSON;
import com.takin.mq.broker.FetchService;
import com.takin.mq.broker.StringProducerData;
import com.takin.rpc.client.ProxyFactory;

public class ConsumerTest {
    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            final FetchService fetch = ProxyFactory.create(FetchService.class, "test", null, null);
            //            for (int i = 0; i < 1; i++) {
            //                producer.send(new StringProducerData("demo").add("Hello jafka"));
            //            }
            StringProducerData data = fetch.fetch("demo", 0);
            System.out.println(JSON.toJSONString(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
