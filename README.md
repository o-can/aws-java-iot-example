# aws-java-iot-example
Starting point example project in Java for the AWS IoT service to be used on a Rasberry Pi or any internet connectivity capable device with an up-to-date Java Runtime Environment.

The code in this example demonstrates how to:
1. Connect to a configurable AWS IoT Endpoint with TLSv1.2.
2. Subscribes to a topic called "MyTopic"
3. Publishes a "Hello World" message to the topic "MyTopic"

## Getting Started

These instructions will get your project up and running on your device for development and testing purposes.

### Prerequisities

Maven is the only requirement for building the project. Dependencies are managed by Maven.

In addition to this software you need to register an IoT device on the AWS IoT dashboard and generate and attach the necessary certificates and policies to it.

For convenience, there are two shell scripts in this project which will setup and tear down an IoT device on the AWS IoT service.

* [create-aws-iot-thing.sh](create-aws-iot-thing.sh)
* [delete-aws-iot-thing.sh](delete-aws-iot-thing.sh])

The create-aws-iot-thing script will also create a ready to go configuration for the example project and download the necessary certificates and keys to the working directory.

Both scripts require a configured aws-cli present, as well as curl and the jq commands.

This will register an IoT Thing called RaspberryPi on the AWS IoT service.
```
chmod +x create-aws-iot-thing.sh
./create-aws-iot-thing.sh RaspberryPi
```

Warning: The delete script is currently deleting the first available certificate. If you have additional certificates, don't use this script to delete the resources.

This will delete IoT Thing called RaspberryPi on the AWS IoT service.

```
chmod +x delete-aws-iot-thing.sh
./delete-aws-iot-thing.sh RaspberryPi
```

If you don't use the create scripts you have to download the client certificate, client private and the root CA certificate yourself.
Furthermore, you have to create a .properties file which contains the necessary information for the client. See [config-example.properties](config-example.properties).
The root CA certificate can be found here:
[https://www.symantec.com/content/en/us/enterprise/verisign/roots/VeriSign-Class%203-Public-Primary-Certification-Authority-G5.pem](https://www.symantec.com/content/en/us/enterprise/verisign/roots/VeriSign-Class%203-Public-Primary-Certification-Authority-G5.pem)

```
# See create-aws-iot-thing.sh for how to get the certs, keys, and endpoint information from the AWS CLI
AWS_IOT_MQTT_HOST = <Your personal endpoint, e.g. *.iot.*.amazonaws.com>
AWS_IOT_MQTT_PORT = <Your personal enpoint port, e.g 8883>
# CLIENT_ID should be the same as THING_NAME
AWS_IOT_MQTT_CLIENT_ID = <Your client id, e.g. RaspberryPi>
AWS_IOT_MY_THING_NAME = <Your thing name, e.g. RaspberryPi>
AWS_IOT_ROOT_CA_FILENAME = <Your root CA certificate filename, e.g. root-ca.pem>
AWS_IOT_CERTIFICATE_FILENAME = <Your certificate filename, e.g. client-cert.pem>
AWS_IOT_PRIVATE_KEY_FILENAME = <Your private key filename, e.g. private-key.pem>

```

### Installing

Creating the package is as simple as

```
mvn clean package
```

This will create an uber-jar (Shade-Plugin) with all dependencies baked into the jar-file.

## Running

```
java -jar aws-java-iot-example.jar PATH_TO_CONFIG_FILE
```

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
