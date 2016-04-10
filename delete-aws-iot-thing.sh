#!/usr/bin/env bash

# Requirements: aws-cli & jq
#
# This script will delete an IoT thing and any attached certificates & policies.

if [ $# -ne 1 ]; then
    echo "Usage: ./delete-aws-iot-thing.sh <Thing>"
    echo "<Thing> is a name for the Thing you would like to delete."
else
    PRINCIPAL_ARN=$(aws iot list-thing-principals --thing-name $1 | jq -r ".principals[0]")
    aws iot detach-thing-principal --thing-name $1 --principal ${PRINCIPAL_ARN}
    aws iot detach-principal-policy --policy-name "PubSubToAnyTopic" --principal ${PRINCIPAL_ARN}
    aws iot delete-policy --policy-name "PubSubToAnyTopic"
    CERTIFICATE_ID=$(aws iot list-certificates | jq -r ".certificates[0].certificateId")
    aws iot update-certificate --certificate-id ${CERTIFICATE_ID} --new-status INACTIVE
    aws iot delete-certificate --certificate-id ${CERTIFICATE_ID}
    aws iot delete-thing --thing-name $1
fi
