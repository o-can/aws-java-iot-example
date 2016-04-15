package de.ozzc.iot.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Helper class encapsulating a Properties Object loaded from a config file.
 *
 * @see Properties
 * @author Ozkan Can
 */
public class IoTConfig {

    public enum ConfigFields {
        AWS_IOT_MQTT_HOST,
        AWS_IOT_MQTT_PORT,
        AWS_IOT_MQTT_CLIENT_ID,
        AWS_IOT_MY_THING_NAME,
        AWS_IOT_ROOT_CA_FILENAME,
        AWS_IOT_CERTIFICATE_FILENAME,
        AWS_IOT_PRIVATE_KEY_FILENAME
    }

    private final Properties properties = new Properties();

    public IoTConfig(final String configFileName) throws IOException {
        properties.load(new FileInputStream(configFileName));
    }

    /**
     * Searches for the property with the specified key in this IoTConfig.
     *
     * The method returns the default value if the the key is not specified or does not contain a value.
     *
     * @param field The key under which to retrieve the value. @See ConfigFields
     * @param defaultValue The default value is returned when they key is not defined in the properties
     * @return String or null, if key is null or value not set.
     */
    public String get(final ConfigFields field, String defaultValue) {
        if(field == null) return null;
        String value = properties.getProperty(field.name(), defaultValue);
        if (value != null) {
            value = value.replace("\"", "");
            value = value.trim();
        }
        return value;
    }

    /**
     * Searches for the property with the specified key in this IoTConfig.
     *
     * @param field The key under which to retrieve the value. @See ConfigFields
     * @return String or null, if key is null or value not set.
     */
    public String get(final ConfigFields field) {
        if(field == null) return null;
        String value = properties.getProperty(field.name());
        if (value != null) {
            value = value.replace("\"", "");
            value = value.trim();
        }
        return value;
    }
}
