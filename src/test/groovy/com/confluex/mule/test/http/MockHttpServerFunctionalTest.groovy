package com.confluex.mule.test.http

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
            fail("Unexpected listener on port ${server.port} after server.stop()")
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
    void shouldAcceptClosureForBody() {
        server.respondTo(path('/')).withBody { ClientRequest request ->
            request.queryParams['chipmunk']
        }

        assert 'simon' == Client.create().resource("http://localhost:${server.port}/").queryParam('chipmunk', 'simon').get(String)
        assert 'theodore' == Client.create().resource("http://localhost:${server.port}/").queryParam('chipmunk', 'theodore').get(String)
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

    @Test
    void waitForShouldBlockUntilRequestCompleted() {
        def finished = false
        Thread.start {
            server.waitFor(path('/go'), 1000)
            finished = true
        }

        assert ! finished
        Client.create().resource("http://localhost:${server.port}/ready").get(ClientResponse.class)
        Thread.sleep(100)
        assert ! finished
        Client.create().resource("http://localhost:${server.port}/steady").get(ClientResponse.class)
        Thread.sleep(100)
        assert ! finished
        Client.create().resource("http://localhost:${server.port}/go").get(ClientResponse.class)
        Thread.sleep(100)
        assert finished
    }

    @Test
    void waitForShouldSupportMultipleRequestsAndConsiderPriorRequests() {
        def areWeThereYet = { Client.create().resource("http://localhost:${server.port}/mom").entity('Are we there yet?') }

        2.times {
            areWeThereYet().post(ClientResponse.class)
        }

        def enoughAlready = false

        Thread.start {
            enoughAlready = server.waitFor(path('/mom').and(body('Are we there yet?')), 10, 2000)
        }

        7.times {
            areWeThereYet().post(ClientResponse.class)
            Thread.sleep(100)
            assert ! enoughAlready
        }

        areWeThereYet().post(ClientResponse.class)
        Thread.sleep(100)
        assert enoughAlready
    }
}
