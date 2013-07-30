package com.confluex.mule.test.http

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.config.ClientConfig
import com.sun.jersey.api.client.config.DefaultClientConfig
import com.sun.jersey.client.urlconnection.HTTPSProperties
import org.junit.Before
import org.junit.Test
import org.springframework.core.io.ClassPathResource

import static com.confluex.mule.test.http.matchers.HttpMatchers.*

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import java.security.KeyStore

class MockHttpsServerFunctionalTest {

    Client sslClient

    @Before
    void initSslClient() {
        ClientConfig config = new DefaultClientConfig()
        SSLContext sslContext = SSLContext.getInstance('SSL')

        KeyStore truststore = KeyStore.getInstance('JKS')
        truststore.load(new ClassPathResource('confluex-mock.keystore').inputStream, 'confluex'.toCharArray())
        TrustManagerFactory tmf = TrustManagerFactory.getInstance('SunX509')
        tmf.init(truststore)

        sslContext.init(null, tmf.getTrustManagers(), null)
        config.properties.put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(null, sslContext))
        sslClient = Client.create(config)
    }

    @Test
    void sameFeaturesShouldWorkWithSsl() {
        def server = new MockHttpsServer(8090)
        server.respondTo(path('/bar')).withStatus(302) // shouldn't get hit
        server.respondTo(path('/foo').and(queryParam('bar', 'baz'))).withBody('success')


        ClientResponse response = sslClient.resource('https://localhost:8090/foo?bar=baz').get(ClientResponse.class)

        server.stop()

        assert 200 == response.status
        assert 'success' == response.getEntity(String.class)
    }
}
