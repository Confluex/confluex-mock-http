package com.confluex.mule.test.http

import com.confluex.mule.test.http.captor.DefaultRequestCaptor
import com.confluex.mule.test.http.captor.RequestCaptor
import com.confluex.mule.test.http.event.EventLatch
import org.junit.Before
import org.junit.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

import static org.mockito.Mockito.*

class MockHttpRequestHandlerTest {

    MockHttpRequestHandler handler

    @Before
    void createMapping() {
        handler = new MockHttpRequestHandler(eventLatch: mock(EventLatch))
    }

    @Test
    void shouldCreateNewMappingWhenGivenUri() {
        assert !handler.mappings
        assert handler.when('/foo') == handler
        assert handler.mappings.size() == 1

        assert handler.when('/bar') == handler
        assert handler.mappings.size() == 2
        assert handler.mappings == [
                '/foo': new DefaultRequestCaptor(),
                '/bar': new DefaultRequestCaptor()
        ]
    }

    @Test
    void shouldSetResourceOfLastMappingAsClasspathResourceWhenProvidedAsString() {
        assert handler.when('/foo').thenReturnResource("/foo.txt") == handler
        assert handler.mappings['/foo'].resource == new ClassPathResource("/foo.txt")
    }

    @Test
    void shouldSetResourceOfLastMappingWhenProvided() {
        def resource = mock(Resource)
        assert handler.when('/foo').thenReturnResource(resource) == handler
        assert handler.mappings['/foo'].resource == resource
    }

    @Test
    void shouldSetTextOfLastMappingWhenProvided() {
        assert handler.when("/foo").thenReturnText("abc") == handler
        assert handler.mappings['/foo'].text == "abc"
    }

    @Test
    void shouldSetStatusOfLastMappingWhenProvided() {
        assert handler.when('/abc').withStatus(123) == handler
        assert handler.mappings['/abc'].status == 123
    }


    @Test
    void shouldBeFluid() {
        def result = handler.when("/foo")
                .thenReturnText("does not compute... beep")
                .withStatus(500)
                .withHeader("x-bender", "Who are you, and why should I care?")
                .withHeader("x-fry", "I did do the nasty in the pasty")
                .when("/bar").thenReturnResource("/data/test.txt")
                .withHeader("Content-Type", "text/plain")



        assert result == handler
        assert result.mappings.size() == 2
        def foo = result.mappings["/foo"]
        assert foo.headers == [
                "x-bender": "Who are you, and why should I care?",
                "x-fry": "I did do the nasty in the pasty"
        ]
        assert foo.text == "does not compute... beep"
        assert foo.status == 500

        def bar = result.mappings["/bar"]
        assert bar.headers["Content-Type"] == "text/plain"
        assert bar.resource == new ClassPathResource("/data/test.txt")

    }

    @Test
    void shouldFindClientRequestsForMappingByUri() {
        def a = [
                new ClientRequest(mock(MockHttpServletRequest)),
                new ClientRequest(mock(MockHttpServletRequest)),
                new ClientRequest(mock(MockHttpServletRequest))
        ]
        def b = [
                new ClientRequest(mock(MockHttpServletRequest)),
                new ClientRequest(mock(MockHttpServletRequest)),
                new ClientRequest(mock(MockHttpServletRequest))
        ]

        handler.mappings["/a"] = new DefaultRequestCaptor(requests:  a)
        handler.mappings["/b"] = new DefaultRequestCaptor(requests:  b)

        assert handler.getRequests("/a") == a
        assert handler.getRequests("/b") == b
        assert handler.getRequests("/other") == []
    }

    @Test
    void shouldDelegateUriRequestHandlingToMappedCaptor() {
        def captor = mock(RequestCaptor)
        handler.mappings["/a/b/c"] = captor
        def request = new MockHttpServletRequest()
        def response = new MockHttpServletResponse()
        handler.handle("/a/b/c", request, response, 1)
        verify(captor).render(request, response)
        verify(handler.eventLatch).addEvent()
    }

    @Test(expected = IllegalArgumentException)
    void shouldErrorIfRequestHandlerIsCalledForUnmappedUri() {
        handler.handle("/a/b/c", new MockHttpServletRequest(), new MockHttpServletResponse(), 1)
    }
}
