package com.confluex.mule.test.http

import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletResponse

class HttpResponderTest {
    HttpResponderBuilder builder
    MockHttpServletResponse response

    @Before void init() {
        builder = new HttpResponderBuilder()
        response = new MockHttpServletResponse()
    }

    @Test
    void shouldSetBodyFromString() {
        builder.withBody('builders are cool')
        builder.responder.render(response)
        assert 'builders are cool' == response.getContentAsString()
    }

    @Test
    void shouldSetBodyFromClasspath() {
        builder.withResource('/http/responses/bar.json')
        builder.responder.render(response)
        assert response.getContentAsString().contains('I\'m a Bar!')
    }

    @Test
    void shouldSetStatus() {
        builder.withStatus(401)
        builder.responder.render(response)
        assert 401 == response.status
    }

    @Test
    void shouldSetHeaders() {
        builder.withHeader("x-bender", "Who are you, and why should I care?")
               .withHeader("x-fry", "I did do the nasty in the pasty")
        builder.responder.render(response)
        assert "Who are you, and why should I care?" == response.getHeader("x-bender")
        assert "I did do the nasty in the pasty" == response.getHeader("x-fry")
    }
}
