package com.confluex.mule.test.http

import com.confluex.mule.test.http.matchers.HttpMatchers
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientHandlerException
import com.sun.jersey.api.client.ClientResponse
import org.junit.After
import org.junit.Before
import org.junit.Test

import static javax.servlet.http.HttpServletResponse.*
import static com.confluex.mule.test.http.matchers.HttpMatchers.*

import static org.junit.Assert.*

class MockHttpServerFunctionalTest {
    private MockHttpServer server

    @Before
    void initServer() {
        server = new MockHttpServer()
    }

    @After
    void stopServer() {
        server.stop()
    }

    @Test
    void newServerShouldBeListening() {
        ClientResponse response = Client.create().resource("http://localhost:${server.port}/").get(ClientResponse.class)
        assert 404 == response.status
    }

    @Test
    void newServerShouldListenOnSpecifiedPort() {
        server = new MockHttpServer(8123)
        ClientResponse response = Client.create().resource("http://localhost:8123/").get(ClientResponse.class)
        assert 404 == response.status
    }

    @Test
    void stopShouldStopListeningOnPort() {
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

    @Test
    void shouldRespondOkWithEmptyBodyByDefault() {
        server.respondTo(anyRequest())
        ClientResponse response = Client.create().resource("http://localhost:${server.port}/").get(ClientResponse.class)
        assert SC_OK == response.status
        assert "" == response.getEntity(String.class)
    }

    @Test
    void differentPathsShouldRespondDifferently() {
        server.respondTo(path('/1')).withBody('one')
        server.respondTo(path('/2')).withResource('/http/responses/two.txt')

        String responseOne = Client.create().resource("http://localhost:${server.port}/1").get(String.class)
        String responseTwo = Client.create().resource("http://localhost:${server.port}/2").get(String.class)

        assert 'one' == responseOne
        assert 'two' == responseTwo
    }

    @Test
    void shouldCaptureRequestInformation() {
        Client.create().resource("http://localhost:${server.port}/cool-api/")
                .header('Accept', 'application/json')
                .entity('{"foo": "bar"}')
                .post(ClientResponse.class)
        Client.create().resource("http://localhost:${server.port}/wicked-api/search?query=foo")
                .header('Accept', 'application/html')
                .header('User-Agent', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:22.0) Gecko/20100101 Firefox/22.0')
                .get(ClientResponse.class)

        assert 2 == server.requests.size()
        assert 1 == server.requests.findAll { it.method == 'GET' }.size()
        assert server.requests.find { it.url =~ /wicked/ }.headers['User-Agent'] =~ 'Macintosh'
        assert server.requests.find { it.headers['Accept'] == 'application/json'}.method == 'POST'

// TODO: make java programmers' lives easier
//        assert server.receivedRequest(path('/cool-api/'))
//        assert server.receivedRequest(header('Accept', 'application/html'))
//        assert server.receivedRequest(header('User-Agent', matching('Mozilla.*')))
//        assert server.receivedRequest(path(matching('wicked.*')))
    }
}
