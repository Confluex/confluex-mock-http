package com.confluex.mule.test.http.captor

import org.junit.Before
import org.junit.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class DefaultRequestCaptorTest {
    DefaultRequestCaptor mapping

    @Before
    void createMapping() {
        mapping = new DefaultRequestCaptor()
    }


    @Test
    void shouldRenderResourceToResponse() {
        def response = new MockHttpServletResponse()
        mapping.resource = new ClassPathResource("/http/responses/bar.json")
        mapping.render(new MockHttpServletRequest(), response)
        assert response.contentAsString == this.class.getResourceAsStream("/http/responses/bar.json").text
    }

    @Test
    void shouldRenderTextToResponseIfNoResourceExists() {
        def response = new MockHttpServletResponse()
        mapping.render(new MockHttpServletRequest(), response)
        assert response.contentAsString == mapping.text
    }

    @Test
    void shouldRenderHeadersToOutputStream() {
        def response = new MockHttpServletResponse()
        mapping.headers = ['a': '1', 'b': '2']
        mapping.render(new MockHttpServletRequest(), response)
        assert response.getHeader('a') == '1'
        assert response.getHeader('b') == '2'
    }

    @Test
    void shouldRenderStatusCodeToOutputStream() {
        def response = new MockHttpServletResponse()
        mapping.render(new MockHttpServletRequest(), response)
        assert response.status == mapping.status
        assert mapping.status
    }

    @Test
    void shouldPreserveClientRequests() {
        def requests = [
                new MockHttpServletRequest("GET", "/foo"),
                new MockHttpServletRequest("GET", "/foo")
        ]
        requests.each { mapping.render(it, new MockHttpServletResponse()) }
        assert mapping.requests.size() == requests.size()
    }

}
