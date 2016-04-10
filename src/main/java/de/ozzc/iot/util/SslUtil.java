package de.ozzc.iot.util;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import java.io.FileReader;
import java.io.IOException;

import java.security.cert.Certificate;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * SSLUtil contains a function to create a SSLSocketFactory to be used by the Mqtt Client for authentication.
 *
 * Created by Ozkan Can on 04/09/2016.
 */
public class SslUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SslUtil.class);

    private static final char[] KEY_STORE_PASSWORD = "Dummy11!!one!".toCharArray();
    private static final String BC_PROVIDER_ID = "BC";
    private static final String TLS_VERSION = "TLSv1.2";

    /**
     * Creates a custom SSLSocketFactory which can be used to authenticate to AWS IoT with Eclipse Paho.
     *
     * @param rootCaCertFile The file path of the root CA file.
     * @param clientCertFile The file path of the client cert file.
     * @param privateKeyFile The file path of the client private key.
     * @return A SSLSocketFactory that validates the server against the root CA file and the client on the server with the client cert and private key.
     * @throws Exception
     */
    public static SSLSocketFactory getSocketFactory(final String rootCaCertFile,
                                                    final String clientCertFile,
                                                    final String privateKeyFile) throws Exception {

        if (Security.getProvider(BC_PROVIDER_ID) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        X509Certificate rootCaCert = getX509Certificate(rootCaCertFile);
        X509Certificate clientCaCert = getX509Certificate(clientCertFile);

        PEMKeyPair clientKeyPair;
        try (PEMParser clientPrivateKeyParser =
                     new PEMParser(new FileReader(privateKeyFile))) {
            clientKeyPair = (PEMKeyPair) clientPrivateKeyParser.readObject();
        } catch (IOException e) {
            LOGGER.error("Failed to load private key file at {} with error: {}", privateKeyFile, e.getMessage());
            throw e;
        }

        // Root CA certificate is used to authenticate the server
        final KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        caKeyStore.load(null, null);
        caKeyStore.setCertificateEntry("ca-certificate", rootCaCert);
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(caKeyStore);

        // client key and certificates are sent to server for client authentication on the server
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(clientKeyPair.getPrivateKeyInfo().getEncoded());
        final PrivateKey clientKey = keyFactory.generatePrivate(spec);

        KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        clientKeyStore.load(null, null);
        clientKeyStore.setCertificateEntry("client", clientCaCert);
        clientKeyStore.setKeyEntry("key", clientKey, KEY_STORE_PASSWORD, new Certificate[]{clientCaCert});
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(clientKeyStore, KEY_STORE_PASSWORD);

        // SSL socket factory
        SSLContext context = SSLContext.getInstance(TLS_VERSION);
        context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        return context.getSocketFactory();
    }

    private static X509Certificate getX509Certificate(final String certFileName) throws CertificateException, IOException {
        try (PEMParser certParser = new PEMParser(new FileReader(certFileName))) {
            return new JcaX509CertificateConverter().setProvider(BC_PROVIDER_ID).getCertificate((X509CertificateHolder) certParser.readObject());
        } catch (IOException | CertificateException e) {
            LOGGER.error("Failed to load certificate file at {} with error: {}", certFileName, e.getMessage());
            throw e;
        }
    }

}
