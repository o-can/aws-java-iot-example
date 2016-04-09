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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.security.cert.Certificate;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 *
 * Created by Ozkan Can on 09.04.2016.
 */
public class SslUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SslUtil.class);

    private static final char[] KEY_STORE_PASSWORD = "11!!one!".toCharArray();
    private static final String BC_PROVIDER_ID = "BC";
    private static final String TLS_VERSION = "TLSv1.2";

    public static SSLSocketFactory getSocketFactory(final String rootCaCertFile, final String clientCertFile, final String privateKeyFile) throws Exception {
        if (Security.getProvider(BC_PROVIDER_ID) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        X509Certificate rootCaCert = getX509Certificate(rootCaCertFile);
        X509Certificate clientCaCert = getX509Certificate(clientCertFile);

        PEMKeyPair clientKeyPair = null;
        try (PEMParser clientPrivateKeyParser = new PEMParser(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(privateKeyFile)))))) {
            clientKeyPair = (PEMKeyPair) clientPrivateKeyParser.readObject();
        } catch (IOException e) {
            LOGGER.error("Failed to load private key file at {} with error: {}", privateKeyFile, e.getMessage());
            throw e;
        }

        // CA certificate is used to authenticate server
        KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        caKeyStore.load(null, null);
        caKeyStore.setCertificateEntry("ca-certificate", rootCaCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caKeyStore);

        // client key and certificates are sent to server so it can authenticate us
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(
                clientKeyPair.getPrivateKeyInfo().getEncoded());
        final KeyFactory kf = KeyFactory.getInstance("RSA");
        final PrivateKey clientKey = kf.generatePrivate(spec);

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("client", clientCaCert);
        ks.setKeyEntry("key", clientKey, KEY_STORE_PASSWORD, new Certificate[]{clientCaCert});
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, KEY_STORE_PASSWORD);

        // finally, create SSL socket factory
        SSLContext context = SSLContext.getInstance(TLS_VERSION);
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return context.getSocketFactory();
    }

    private static X509Certificate getX509Certificate(final String certFileName) throws CertificateException, IOException {
        try (PEMParser certParser = new PEMParser(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(certFileName)))))) {
            return new JcaX509CertificateConverter().setProvider(BC_PROVIDER_ID).getCertificate((X509CertificateHolder) certParser.readObject());
        } catch (IOException | CertificateException e) {
            LOGGER.error("Failed to load certificate file at {} with error: {}", certFileName, e.getMessage());
            throw e;
        }
    }

}
