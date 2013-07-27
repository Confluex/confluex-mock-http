package com.confluex.mule.test.http

import org.mortbay.jetty.Server

class MockHttpServer {

    int port
    Server jettyServer

    MockHttpServer() {
        this(0)
    }

    MockHttpServer(int port) {
        this.port = port
        if (0 == port) findAvailablePort()
        jettyServer = new Server(this.port)
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
}
