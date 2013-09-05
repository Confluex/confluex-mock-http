package com.confluex.mule.test.http

import com.confluex.mule.test.http.event.MatchingEventLatch
import com.confluex.mule.test.http.matchers.HttpRequestMatcher
import groovy.transform.ToString
import org.mortbay.jetty.handler.AbstractHandler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ToString(includeNames = true, includes = "mappings, currentMapping")
class MockHttpRequestHandler extends AbstractHandler {
    List<HttpRequestMatcher> matchers = []
    Map<HttpRequestMatcher, HttpResponder> responders = [:]
    List<ClientRequest> requests = []
    List<MatchingEventLatch> latches = []

    void handle(String uri, HttpServletRequest request, HttpServletResponse response, int dispatch) {
        def clientRequest = new ClientRequest(request)
        requests << clientRequest
        HttpRequestMatcher matcher = matchers.find { matcher ->
            matcher.matches(clientRequest)
        }
        responders[matcher]?.render(clientRequest, response)
        synchronized(latches) {
            latches.each {
                it.addEvent(clientRequest)
            }
        }
    }

    HttpResponderBuilder respondTo(HttpRequestMatcher matcher) {
        matchers.add 0, matcher
        HttpResponderBuilder builder = new HttpResponderBuilder()
        responders[matcher] = builder.responder
        return builder
    }

    boolean waitFor(HttpRequestMatcher matcher, int expected, long timeoutMs) {
        def latch
        synchronized(latches) {
            latch = new MatchingEventLatch(matcher, expected)
            requests.each { latch.addEvent(it) }
            latches << latch
        }
        latch.await(timeoutMs)
    }
}