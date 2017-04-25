package yanfq;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by yanfq on 17-4-25.
 */
public class MqttPublishServer {
    String TOPIC = "mqtt_topic";
    String HOST = "tcp://192.168.0.65:1883";
    String clientId = "publishServer";
    private MqttClient publishClient;
    private static MqttTopic topic;
    private static MqttMessage message;

    public MqttPublishServer() throws MqttException {
        // MemoryPersistence设置clientid的保存形式，默认为以内存保存
        publishClient = new MqttClient(HOST, clientId, new MemoryPersistence());
        connect();
        newMessage();
    }

    private void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        // 设置超时时间
        options.setConnectionTimeout(10);
        // 设置会话心跳时间
        options.setKeepAliveInterval(20);
        try {
            publishClient.setCallback(new PushCallback());
            publishClient.connect(options);
            topic = publishClient.getTopic(TOPIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newMessage(){
        message = new MqttMessage();
        message.setQos(2);
        message.setRetained(true);
    }

    public static void publish(MqttTopic topic, MqttMessage message) throws MqttPersistenceException,
            MqttException {
        MqttDeliveryToken token = topic.publish(message);
        token.waitForCompletion();
        System.out.println("message is published completely! " + token.isComplete());
    }

    public static void execute() {
        System.out.println("----------------------------------------------run-----");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        try{
                            long timeMillis =System.currentTimeMillis();
                            String str = "==发布端发送的时间为=="+timeMillis;
                            message.setPayload(str.getBytes());
                            publish(topic, message);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },0,2000, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) throws MqttException {
        new MqttPublishServer();
        MqttPublishServer.execute();
    }
}
