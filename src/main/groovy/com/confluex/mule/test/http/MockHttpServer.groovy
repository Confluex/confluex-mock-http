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
        jettyServer = new Server(this.port)
        jettyServer.handler = handler = new MockHttpRequestHandler()
        jettyServer.start()
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
}
