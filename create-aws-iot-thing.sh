#!/usr/bin/env bash

# Requirements: aws-cli & jq
#
# This script will register an IoT thing. Create, download and attach the keys and certificates and attach an all topics/actions policy to the certificates and the IoT Thing.

if [ $# -eq 1 ]; then
    echo "Usage: ./create-aws-iot-thing.sh <Thing>"
    echo "<Thing> is a name for the Thing you would like to create."
else
    aws iot create-thing --thing-name $1
    CERTIFICATE_ARN=$(aws iot create-keys-and-certificate --set-as-active --certificate-pem-outfile client-cert.pem --public-key-outfile public-key.pem --private-key-outfile private-key.pem | jq -r ".certificateArn")
    curl -s -S https://www.symantec.com/content/en/us/enterprise/verisign/roots/VeriSign-Class%203-Public-Primary-Certification-Authority-G5.pem -o root-ca.pem
    aws iot create-policy --policy-name "PubSubToAnyTopic" --policy-document file://Device-Policy.json
    aws iot attach-principal-policy --principal $CERTIFICATE_ARN --policy-name "PubSubToAnyTopic"
    aws iot attach-thing-principal --thing-name $1 --principal $CERTIFICATE_ARN
    aws iot describe-endpoint
fi


