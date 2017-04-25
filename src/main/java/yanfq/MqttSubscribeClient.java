package yanfq;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import yanfq.PushCallback;

/**
 * Created by yanfq on 17-4-25.
 */
public class MqttSubscribeClient {
    static String TOPIC = "mqtt_topic";
    static String HOST = "tcp://192.168.0.65:1883";
    static String clientId = "clientId1";
    private static MqttClient subscribeClient;
    private static MqttConnectOptions options;
    private static MqttTopic topic;

    public MqttSubscribeClient() throws Exception{
        connect();
    }

    private static void connect() {
        try {
            subscribeClient = new MqttClient(HOST, clientId, new MemoryPersistence());
            options = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            subscribeClient.setCallback(new PushCallback());
            topic = subscribeClient.getTopic(TOPIC);
            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
            options.setWill(topic, "close".getBytes(), 2, true);
            subscribeClient.connect(options);
            int[] Qos = {2};
            String[] topic = {TOPIC};
            subscribeClient.subscribe(topic, Qos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        new MqttSubscribeClient();
    }
}
