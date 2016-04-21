package de.ozzc.iot.example;

import com.google.gson.Gson;
import de.ozzc.iot.model.ErrorResponse;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by Ozkan Can on 10/04/16.
 */
class ExampleCallback implements MqttCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleCallback.class);

    private final String clientId;
    private final String shadowGetRejectedTopic;
    private final Gson gson = new Gson();

    public ExampleCallback(String clientId)
    {
        if(clientId == null)
            throw new IllegalArgumentException("clientId cannot be null");
        this.clientId = clientId;
        this.shadowGetRejectedTopic = "$aws/things/" + clientId + "/shadow/get/rejected";
    }


    @Override
    public void connectionLost(Throwable cause) {
        LOGGER.info("Connection Lost.", cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        boolean arrivedOnUnknownTopic = true;
        if(topic.equals(shadowGetRejectedTopic))
        {
            arrivedOnUnknownTopic = false;
            if(message != null)
            {
                byte[] payload = message.getPayload();
                if(payload != null) {
                    String json = new String(payload);
                    ErrorResponse errorResponse = gson.fromJson(json, ErrorResponse.class);
                    LOGGER.info(errorResponse.toString());
                }
            }
        }
        if(arrivedOnUnknownTopic)
        {
                LOGGER.info("Message arrived on topic {}. Contents: {}", topic, new String(message.getPayload()));
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOGGER.info("Completed delivery of message with id {}", token.getMessageId());
    }
}
