package com.confluex.mule.test.http

import com.confluex.mule.test.http.captor.DefaultRequestCaptor
import com.confluex.mule.test.http.captor.RequestCaptor
import com.confluex.mule.test.http.event.DefaultEventLatch
import com.confluex.mule.test.http.event.EventLatch
import com.confluex.mule.test.http.expectations.Expectation
import com.confluex.mule.test.http.matchers.HttpRequestMatcher
import groovy.transform.ToString
import org.mortbay.jetty.handler.AbstractHandler
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.junit.Assert.*

@ToString(includeNames = true, includes = "mappings, currentMapping")
class MockHttpRequestHandler extends AbstractHandler implements EventLatch {
    RequestCaptor currentMapping;
    Map<String, RequestCaptor> mappings = [:]
    List<HttpRequestMatcher> matchers = []
    Map<HttpRequestMatcher, HttpResponder> responders = [:]
    List<ClientRequest> requests = []

    @SuppressWarnings("GroovyUnusedDeclaration")
    @Delegate
    EventLatch eventLatch = new DefaultEventLatch()

    MockHttpRequestHandler when(String uri) {
        currentMapping = new DefaultRequestCaptor()
        mappings[uri] = currentMapping
        return this
    }

    MockHttpRequestHandler thenReturnResource(String path) {
        return thenReturnResource(new ClassPathResource(path))
    }

    MockHttpRequestHandler thenReturnResource(Resource resource) {
        currentMapping.resource = resource
        return this
    }

    MockHttpRequestHandler thenReturnText(String text) {
        currentMapping.text = text
        return this
    }

    MockHttpRequestHandler withStatus(Integer code) {
        currentMapping.status = code
        return this
    }

    MockHttpRequestHandler withHeader(String key, String value) {
        currentMapping.headers[key] = value
        return this
    }

    void handle(String uri, HttpServletRequest request, HttpServletResponse response, int dispatch) {
        HttpRequestMatcher matcher = matchers.find { matcher ->
            matcher.matches(request)
        }
        responders[matcher]?.render(response)
        requests << new ClientRequest(request)
        addEvent()
    }

    MockHttpRequestHandler verify(String uri, Expectation... expectations) {
        def uriRequests = getRequests(uri)
        uriRequests.each { req ->
            expectations.eachWithIndex { expectation, i ->
                if (!expectation.verify(req)) {
                    fail("Expectation: ${expectation} failed for request #${i + 1}: ${req}")
                }
            }
        }
        return this
    }

    HttpResponderBuilder respondTo(HttpRequestMatcher matcher) {
        matchers << matcher
        HttpResponderBuilder builder = new HttpResponderBuilder()
        responders[matcher] = builder.responder
        return builder
    }

    List<ClientRequest> getRequests(String uri) {
        return mappings[uri]?.requests ?: []
    }

}