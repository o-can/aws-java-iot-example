package de.ozzc.iot.example;

import de.ozzc.iot.util.SslUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by Ozkan Can on 09.04.2016.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        final int qos = 0;
        final String topicName = "MyTopic";
        final String message = "Hello World!";
        final long quiesceTimeout = 5000;

        // This information we get from AWS after registering and activating a thing
        final String thingName = "MyNewThing";
        final String serverUrl = "ssl://*.iot.*.amazonaws.com:8883";
        final String rootCaCertFile = "root-ca.pem.key";
        final String clientCertFile = "*-certificate.pem.crt";
        final String clientPrivateKeyFile = "*-private.pem.key";

        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setSocketFactory(SslUtil.getSocketFactory(rootCaCertFile, clientCertFile, clientPrivateKeyFile));
            options.setCleanSession(true);

            MqttClient client = new MqttClient(serverUrl, thingName);
            client.setCallback(new ExampleCallback());
            client.connect(options);
            client.subscribe(topicName, qos);
            client.publish(topicName, new MqttMessage(message.getBytes()));
            client.disconnect(quiesceTimeout);
            client.close();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
