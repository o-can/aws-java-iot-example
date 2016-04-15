package de.ozzc.iot.example;

import de.ozzc.iot.util.IoTConfig;
import de.ozzc.iot.util.SslUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocketFactory;

import static de.ozzc.iot.util.IoTConfig.ConfigFields.*;

/**
 * Simple MQTT Client Example for Publish/Subscribe on AWS IoT.
 * This example should serve as a starting point for using AWS IoT with Java.
 *
 * <ul>
 *  <li>The client connects to the endpoint specified in the config file.</li>
 *  <li>Subscribes to the topic "MyTopic".</li>
 *  <li>Publishes  a "Hello World" message to the topic "MyTopic.</li>
 *  <li>Closes the connection.</li>
 *  <li>This example should serve as a starting point for using AWS IoT with Java.</li>
 * </ul>
 * Created by Ozkan Can on 04/09/2016.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final int QOS_LEVEL0 = 0;
    private static final int QOS_LEVEL1 = 1;
    private static final String TOPIC = "MyTopic";
    private static final String MESSAGE = "Hello World!";
    private static final byte[] EMPTY_MESSAGE = "".getBytes();
    private static final long QUIESCE_TIMEOUT = 5000;

    public static void main(String[] args) {

        if(args.length < 1)
        {
            showHelp();
        }

        try {
            IoTConfig config = new IoTConfig(args[0]);
            SSLSocketFactory sslSocketFactory = SslUtil.getSocketFactory(
                    config.get(AWS_IOT_ROOT_CA_FILENAME),
                    config.get(AWS_IOT_CERTIFICATE_FILENAME),
                    config.get(AWS_IOT_PRIVATE_KEY_FILENAME));
            MqttConnectOptions options = new MqttConnectOptions();
            options.setSocketFactory(sslSocketFactory);

            // AWS IoT message broker does not support persistent sessions as of writing (04/15/2016)
            // Client will be disconnected from message broker if the clean session attribute is set to false
            // http://docs.aws.amazon.com/iot/latest/developerguide/protocols.html
            options.setCleanSession(true);

            final String serverURI = "ssl://"+config.get(AWS_IOT_MQTT_HOST)+":"+config.get(AWS_IOT_MQTT_PORT);
            final String clientId = config.get(AWS_IOT_MQTT_CLIENT_ID);

            // AWS IoT does not support persistent sessions, therefore we use MemoryPersistence
            MqttAsyncClient asyncClient = new MqttAsyncClient(serverURI, clientId, new MemoryPersistence());
            asyncClient.connect(options);
            asyncClient.subscribe(TOPIC, QOS_LEVEL0);
            asyncClient.publish(TOPIC, new MqttMessage(MESSAGE.getBytes()));

            //Shadow State Get
            final String shadowGetTopic = "$aws/things/"+clientId+"/shadow/get";
            final String shadowGetAcceptedTopic = "$aws/things/"+clientId+"/shadow/get/accepted";
            final String shadowGetRejectedTopic = "$aws/things/"+clientId+"/shadow/get/rejected";
            asyncClient.subscribe(shadowGetAcceptedTopic, QOS_LEVEL1);
            asyncClient.subscribe(shadowGetRejectedTopic, QOS_LEVEL1);


            MqttMessage EMPTY_MQTT_MESSAGE = new MqttMessage(EMPTY_MESSAGE);
            EMPTY_MQTT_MESSAGE.setQos(QOS_LEVEL1);
            asyncClient.publish(shadowGetTopic, EMPTY_MQTT_MESSAGE);


            // Remove the disconnect and close, if you want to continue listening/subscribing
            //client.disconnect(QUIESCE_TIMEOUT);
            //client.close();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(-1);
        }
    }

    private static void showHelp()
    {
        System.out.println("Usage: java -jar aws-iot-java-example.jar <config-file>");
        System.out.println("\nSee config-example.properties for an example of a config file.");
        System.exit(0);
    }
}
