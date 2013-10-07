package com.confluex.mock.http

import com.confluex.mock.http.jetty.MockSslSocketConnector
import org.mortbay.jetty.Server
import org.springframework.core.io.ClassPathResource

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import java.security.KeyStore

class MockHttpsServer extends MockHttpServer {
    MockHttpsServer() {
        super()
    }

    MockHttpsServer(int port) {
        super(port)
    }

    @Override
    protected Server initJettyServer(final int port) {
        new Server() {{
            addConnector(new MockSslSocketConnector() {{
                setKeystore("confluex-mock.keystore")
                setTruststore("confluex-mock.keystore")
                setPassword("confluex")
                setKeyPassword("confluex")
                setTrustPassword("confluex")
                setMaxIdleTime(120000)
                setPort(port)
            }});
        }}
    }

    static SSLContext getClientSslContext() {
        SSLContext sslContext = SSLContext.getInstance('SSL')

        sslContext.init(null, getTrustManagerFactory().getTrustManagers(), null)
        return sslContext
    }

    static TrustManagerFactory getTrustManagerFactory() {
        KeyStore truststore = KeyStore.getInstance('JKS')
        truststore.load(new ClassPathResource('confluex-mock.keystore').inputStream, 'confluex'.toCharArray())
        TrustManagerFactory tmf = TrustManagerFactory.getInstance('SunX509')
        tmf.init(truststore)
        return tmf
    }
}
