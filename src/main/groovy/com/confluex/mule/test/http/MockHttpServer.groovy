package com.confluex.mule.test.http

import com.confluex.mule.test.http.matchers.HttpRequestMatcher
import org.mortbay.jetty.Server

class MockHttpServer {

    int port
    Server jettyServer
    MockHttpRequestHandler handler

    MockHttpServer() {
        this(0)
    }

    MockHttpServer(int port) {
        this.port = port
        if (0 == port) findAvailablePort()
        jettyServer = initJettyServer(this.port)
        jettyServer.handler = handler = new MockHttpRequestHandler()
        jettyServer.start()
    }

    protected Server initJettyServer(int port) {
        new Server(port)
    }

    private void findAvailablePort() {
        def socket = new ServerSocket(0)
        port = socket.getLocalPort()
        socket.close()
    }

    void stop() {
        jettyServer.stop()
    }

    HttpResponderBuilder respondTo(HttpRequestMatcher matcher) {
        handler.respondTo(matcher)
    }

    void waitFor(HttpRequestMatcher matcher, Long timeoutMs) {
        waitFor(matcher, 1, timeoutMs)
    }

    boolean waitFor(HttpRequestMatcher matcher, int expected, Long timeoutMs) {
        handler.waitFor(matcher, expected, timeoutMs)
    }

    List<ClientRequest> getRequests() {
        handler.requests
    }
}
