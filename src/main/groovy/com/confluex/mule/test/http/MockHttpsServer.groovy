package com.confluex.mule.test.http

import com.confluex.mule.test.http.jetty.MockSslSocketConnector
import org.mortbay.jetty.Server

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
}
