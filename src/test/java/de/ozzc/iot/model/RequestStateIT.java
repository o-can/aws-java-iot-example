package de.ozzc.iot.model;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Scanner;
import java.util.UUID;

/**
 * Created by ocan on 21.04.2016.
 */
public class RequestStateIT {

    public static final String rootCertLink = "https://www.symantec.com/content/en/us/enterprise/verisign/roots/VeriSign-Class%203-Public-Primary-Certification-Authority-G5.pem";
    private AWSIotClient iotClient = null;
    private CreateKeysAndCertificateResult createKeysAndCertificateResult = null;
    private String thingName;
    private String policyName;
    private DescribeEndpointResult describeEndpointResult = null;
    private String rootCert = "";

    @Before
    public void setUp() throws Exception {
        thingName = "Test-Thing-"+UUID.randomUUID().toString().replace("-", "");
        policyName = "Test-Policy-"+UUID.randomUUID().toString().replace("-", "");
        URL rootCertURL = new URL(rootCertLink);
        Scanner scanner = new Scanner(rootCertURL.openStream()).useDelimiter("\\A");
        rootCert = scanner.next();
        scanner.close();

        iotClient = new AWSIotClient(new DefaultAWSCredentialsProviderChain());
        iotClient.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
        iotClient.createThing(new CreateThingRequest().withThingName(thingName));
        createKeysAndCertificateResult = iotClient.createKeysAndCertificate(new CreateKeysAndCertificateRequest().withSetAsActive(true));
        String policy = new String(Files.readAllBytes(Paths.get("Device-Policy.json")));
        iotClient.createPolicy(new CreatePolicyRequest().withPolicyDocument(policy).withPolicyName(policyName));
        iotClient.attachPrincipalPolicy(new AttachPrincipalPolicyRequest().withPolicyName(policyName).withPrincipal(createKeysAndCertificateResult.getCertificateArn()));
        iotClient.attachThingPrincipal(new AttachThingPrincipalRequest().withThingName(thingName).withPrincipal(createKeysAndCertificateResult.getCertificateArn()));
        describeEndpointResult = iotClient.describeEndpoint(new DescribeEndpointRequest());
    }

    @After
    public void tearDown() throws Exception {
        if(iotClient != null)
        {
            if(createKeysAndCertificateResult != null)
            {
                iotClient.detachThingPrincipal(new DetachThingPrincipalRequest().withPrincipal(createKeysAndCertificateResult.getCertificateArn()).withThingName(thingName));
                iotClient.detachPrincipalPolicy(new DetachPrincipalPolicyRequest().withPolicyName(policyName).withPrincipal(createKeysAndCertificateResult.getCertificateArn()));
                iotClient.updateCertificate(new UpdateCertificateRequest().withNewStatus(CertificateStatus.INACTIVE).withCertificateId(createKeysAndCertificateResult.getCertificateId()));
                iotClient.deleteCertificate(new DeleteCertificateRequest().withCertificateId(createKeysAndCertificateResult.getCertificateId()));
            }
            iotClient.deletePolicy(new DeletePolicyRequest().withPolicyName(policyName));
            iotClient.deleteThing(new DeleteThingRequest().withThingName(thingName));
            iotClient.shutdown();
        }

    }

    @Test
    public void testJson() {
    }


}