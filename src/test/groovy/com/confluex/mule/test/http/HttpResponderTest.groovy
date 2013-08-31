package com.confluex.mule.test.http

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import org.springframework.mock.web.MockHttpServletResponse

import static org.mockito.Mockito.*

@RunWith(MockitoJUnitRunner)
class HttpResponderTest {
    HttpResponderBuilder builder
    MockHttpServletResponse response
    @Mock
    ClientRequest request

    @Before void init() {
        builder = new HttpResponderBuilder()
        response = new MockHttpServletResponse()
    }

    @Test
    void shouldSetBodyFromString() {
        builder.withBody('builders are cool')
        builder.responder.render(request, response)
        assert 'builders are cool' == response.getContentAsString()
    }

    @Test
    void shouldSetBodyFromClasspath() {
        builder.withResource('/http/responses/bar.json')
        builder.responder.render(request, response)
        assert response.getContentAsString().contains('I\'m a Bar!')
    }

    @Test
    void shouldSetStatus() {
        builder.withStatus(401)
        builder.responder.render(request, response)
        assert 401 == response.status
    }

    @Test
    void shouldSetHeaders() {
        builder.withHeader("x-bender", "Who are you, and why should I care?")
               .withHeader("x-fry", "I did do the nasty in the pasty")
        builder.responder.render(request, response)
        assert "Who are you, and why should I care?" == response.getHeader("x-bender")
        assert "I did do the nasty in the pasty" == response.getHeader("x-fry")
    }

    @Test
    void shouldSetBodyWithClosure() {
        when(request.getQueryParams()).thenReturn( [thing: 'one'] )
        when(request.getHeaders()).thenReturn( [thing: 'two'] )
        when(request.getBody()).thenReturn('three')

        builder.withBody() { ClientRequest request ->
            request.queryParams['thing'] + request.headers['thing'] + request.body
        }

        builder.responder.render(request, response)
        assert 'onetwothree' == response.getContentAsString()
    }
}
