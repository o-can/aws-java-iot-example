#!/usr/bin/env bash

# Requirements: aws-cli & jq
#
# This script will register an IoT thing. Create, download and attach the keys and certificates and attach an all topics/actions policy to the certificates and the IoT Thing.

if [ $# -ne 1 ]; then
    echo "Usage: ./create-aws-iot-thing.sh <Thing>"
    echo "<Thing> is a name for the IoT Thing you would like to create."
else
    aws iot create-thing --thing-name $1
    CERTIFICATE_ARN=$(aws iot create-keys-and-certificate --set-as-active --certificate-pem-outfile client-cert.pem --public-key-outfile public-key.pem --private-key-outfile private-key.pem | jq -r ".certificateArn")
    curl -s -S https://www.symantec.com/content/en/us/enterprise/verisign/roots/VeriSign-Class%203-Public-Primary-Certification-Authority-G5.pem -o root-ca.pem
    aws iot create-policy --policy-name "PubSubToAnyTopic" --policy-document file://Device-Policy.json
    aws iot attach-principal-policy --principal ${CERTIFICATE_ARN} --policy-name "PubSubToAnyTopic"
    aws iot attach-thing-principal --thing-name $1 --principal ${CERTIFICATE_ARN}
    IOT_ENDPOINT=$(aws iot describe-endpoint | jq -r ".endpointAddress")
    echo -e "AWS_IOT_MQTT_HOST = ${IOT_ENDPOINT}\n" > config.properties
    echo -e "AWS_IOT_MQTT_PORT = 8883\n" >> config.properties
    echo -e "AWS_IOT_MQTT_CLIENT_ID = $1\n" >> config.properties
    echo -e "AWS_IOT_MY_THING_NAME = $1\n" >> config.properties
    echo -e "AWS_IOT_ROOT_CA_FILENAME = $(pwd)/root-ca.pem\n" >> config.properties
    echo -e "AWS_IOT_CERTIFICATE_FILENAME = $(pwd)/client-cert.pem\n" >> config.properties
    echo -e "AWS_IOT_PRIVATE_KEY_FILENAME = $(pwd)/private-key.pem\n" >> config.properties
fi
