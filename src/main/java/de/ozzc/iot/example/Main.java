package de.ozzc.iot.example;

import de.ozzc.iot.util.SslUtil;
import org.eclipse.paho.client.mqttv3.*;

/**
 *
 * Created by Ozkan Can on 09.04.2016.
 */
public class Main {


    public static void main(String[] args) throws Exception {

        String serverUrl = "ssl://*.iot.eu-central-1.amazonaws.com:8883";
        MqttClient client = new MqttClient(serverUrl, "MyNewThing", null);
        client.setCallback(new MyCallback());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setConnectionTimeout(60);
        options.setKeepAliveInterval(60);
        options.setSocketFactory(SslUtil.getSocketFactory("root-ca.pem.key", "*-certificate.pem.crt", "*-private.pem.key"));
        client.connect(options);
        client.subscribe("MyTopic", 0);
        client.publish("MyTopic", new MqttMessage("Hello World".getBytes()));
        client.close();
    }

    private static class MyCallback implements MqttCallback {
        @Override
        public void connectionLost(Throwable cause) {
            System.out.println("connectionLost");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            System.out.println("Message Arrive. Topic : " + topic + " , Message : " + message.toString());
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            System.out.println("deliveryComplete");
        }
    }
}
