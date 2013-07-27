package com.confluex.mule.test.http

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientHandlerException
import com.sun.jersey.api.client.ClientResponse
import org.junit.Test

import static org.junit.Assert.*

class MockHttpServerFunctionalTest {

    @Test
    void newServerShouldBeListening() {
        MockHttpServer server = new MockHttpServer()
        ClientResponse response = Client.create().resource("http://localhost:${server.port}/").get(ClientResponse.class)
        assert 404 == response.status
    }

    @Test
    void newServerShouldListenOnSpecifiedPort() {
        MockHttpServer server = new MockHttpServer(8123)
        ClientResponse response = Client.create().resource("http://localhost:8123/").get(ClientResponse.class)
        assert 404 == response.status
    }

    @Test
    void stopShouldStopListeningOnPort() {
        MockHttpServer server = new MockHttpServer()
        server.stop()
        try {
            Client.create().resource("http://localhost:${server.port}/").get(ClientResponse.class)
            fail("Should have thrown ClientHandlerException with cause ConnectException")
        } catch (ClientHandlerException e) {
            if (! e.cause instanceof ConnectException) {
                throw e
            }
        }
    }
}
