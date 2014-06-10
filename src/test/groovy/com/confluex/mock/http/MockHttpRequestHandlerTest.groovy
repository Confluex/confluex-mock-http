package com.confluex.mock.http

import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

import static com.confluex.mock.http.matchers.HttpMatchers.*

class MockHttpRequestHandlerTest {
    MockHttpRequestHandler handler

    @Before
    void createHandler() {
        handler = new MockHttpRequestHandler()
    }

    @Test
    void shouldHandleConcurrentRequestsWhileWaitingForLatch() {
        def uri = "/test/concurrent"
        def requestCount = 1000
        def threads = []
        def errors = []
        def matcher = anyRequest()
        handler.matchers << matcher
        requestCount.times {
            threads << Thread.start {
                def request = new MockHttpServletRequest()
                def response = new MockHttpServletResponse()
                try {
                    handler.handle(uri, request, response, 0)
                } catch (e) {
                    errors << e
                }
            }

        }
        assert handler.waitFor(matcher, requestCount, 10000)
    }
}
