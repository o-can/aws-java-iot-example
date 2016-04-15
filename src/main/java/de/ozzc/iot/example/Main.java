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
 *  <li>Creates a device shadow state and updates it.</li>
 *  <li>Closes the connection.</li>
 * </ul>
 * @author Ozkan Can
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String TOPIC = "MyTopic";

    private static final MqttMessage HELLO_WORLD_MQTT_MESSAGE = new MqttMessage("Hello World!".getBytes());
    private static final MqttMessage EMPTY_MQTT_MESSAGE = new MqttMessage("".getBytes());


    // AWS IoT deviates from the MQTT Specification on QOS 0
    // Specification: Message is delivered at most once (zero or one times).
    // AWS IoT: Message is delivered zero or more(!) times.
    private static final int QOS_0 = 0;

    // Specification: Message is delivered at  least once (one or more times)
    // Default QoS for MqttMessage
    private static final int QOS_1 = 1;

    // AWS IoT does not support subscribing or publishing with QOS 2.
    // Specification: Message is delivered once.
    // private static final int QOS_2 = 2;

    // AWS IoT message broker does not support persistent sessions as of writing (04/15/2016)
    // Client will be disconnected from message broker if the clean session attribute is set to false
    // http://docs.aws.amazon.com/iot/latest/developerguide/protocols.html
    private static final boolean CLEAN_SESSION = true;

    // Rime in milliseconds to allow for existing work to finish before disconnecting
    // @see org.eclipse.paho.client.mqttv3.IMqttAsyncClient#disconnect(long)
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
            options.setCleanSession(CLEAN_SESSION);

            final String serverURI = "ssl://"+config.get(AWS_IOT_MQTT_HOST)+":"+config.get(AWS_IOT_MQTT_PORT);
            final String clientId = config.get(AWS_IOT_MQTT_CLIENT_ID);

            // AWS IoT does not support persistent sessions, therefore we use MemoryPersistence
            MqttAsyncClient asyncClient = new MqttAsyncClient(serverURI, clientId, new MemoryPersistence());
            asyncClient.connect(options).waitForCompletion();
            if(asyncClient.isConnected()) {

                asyncClient.subscribe(TOPIC, QOS_1);
                asyncClient.publish(TOPIC, HELLO_WORLD_MQTT_MESSAGE);

                //Shadow State Get
                final String shadowGetTopic = "$aws/things/" + clientId + "/shadow/get";
                final String shadowGetAcceptedTopic = "$aws/things/" + clientId + "/shadow/get/accepted";
                final String shadowGetRejectedTopic = "$aws/things/" + clientId + "/shadow/get/rejected";
                asyncClient.subscribe(shadowGetAcceptedTopic, QOS_1);
                asyncClient.subscribe(shadowGetRejectedTopic, QOS_1);


                asyncClient.publish(shadowGetTopic, EMPTY_MQTT_MESSAGE);

                // Remove the disconnect and close, if you want to continue listening/subscribing
                asyncClient.disconnect(QUIESCE_TIMEOUT).waitForCompletion();
                asyncClient.close();
            }
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
