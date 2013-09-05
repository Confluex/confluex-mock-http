package com.confluex.mock.http.jetty

import org.mortbay.jetty.security.Password
import org.mortbay.jetty.security.SslSocketConnector
import org.springframework.core.io.ClassPathResource

import javax.net.ssl.KeyManager
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLServerSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import java.security.KeyStore
import java.security.SecureRandom

/**
 * Specific to Jetty v6
 *
 * Uses only the classpath to locate the keystore and truststore
 */
public class MockSslSocketConnector extends SslSocketConnector {
    private transient Password _password;
    private transient Password _keyPassword;
    private transient Password _trustPassword;

    MockSslSocketConnector() {
        super()
    }

    @Override
    protected SSLServerSocketFactory createFactory()
    throws Exception
    {
        if (getTruststore()==null)
        {
            setTruststore(getKeystore())
            setTruststoreType(getKeystoreType())
        }

        KeyManager[] keyManagers = null;
        InputStream keystoreInputStream = null;
        KeyStore keyStore = null;
        try
        {
            if (getKeystore() != null)
            {
                keystoreInputStream = new ClassPathResource(getKeystore()).getInputStream();
            }

            keyStore = KeyStore.getInstance(getKeystoreType());
            keyStore.load(keystoreInputStream, _password == null ? null : _password.toString().toCharArray());
        }
        finally
        {
            if (keystoreInputStream != null)
            {
                keystoreInputStream.close();
            }
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(getSslKeyManagerFactoryAlgorithm());
        keyManagerFactory.init(keyStore,_keyPassword==null?null:_keyPassword.toString().toCharArray());
        keyManagers = keyManagerFactory.getKeyManagers();

        TrustManager[] trustManagers = null;
        InputStream truststoreInputStream = null;
        KeyStore trustStore = null;

        try
        {
            if (getTruststore() != null)
            {
                truststoreInputStream = new ClassPathResource(getTruststore()).getInputStream()
            }
            trustStore = KeyStore.getInstance(getTruststoreType());
            trustStore.load(truststoreInputStream, _trustPassword == null ? null : _trustPassword.toString().toCharArray());
        }
        finally
        {
            if (truststoreInputStream != null)
            {
                truststoreInputStream.close();
            }
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(getSslTrustManagerFactoryAlgorithm());
        trustManagerFactory.init(trustStore);
        trustManagers = trustManagerFactory.getTrustManagers();


        SecureRandom secureRandom = getSecureRandomAlgorithm()==null?null:SecureRandom.getInstance(getSecureRandomAlgorithm());

        SSLContext context = getProvider()==null?SSLContext.getInstance(getProtocol()):SSLContext.getInstance(getProtocol(), getProvider());

        context.init(keyManagers, trustManagers, secureRandom);

        return context.getServerSocketFactory();
    }

    @Override
    public void setPassword(String password) {
        _password = new Password(password)
        super.setPassword(password)
    }

    @Override
    public void setKeyPassword(String password) {
        _keyPassword = new Password(password)
        super.setKeyPassword(password)
    }

    @Override
    public void setTrustPassword(String password) {
        _trustPassword = new Password(password)
        super.setTrustPassword(password)
    }
}