package com.confluex.mule.test.http.expectations

import com.confluex.mule.test.http.ClientRequest
import org.junit.Before
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest


class MediaTypeExpectationTest {
    protected MediaTypeExpectation expectation

    @Before
    void createExpectation() {
        expectation = MediaTypeExpectation.XML
    }

    @Test
    void shouldVerifyIfMatchesExpectation() {
        def request = new MockHttpServletRequest("GET", "/test")
        request.contentType = MediaType.APPLICATION_XML_VALUE
        assert expectation.verify(new ClientRequest(request))
    }

    @Test
    void shouldVerifyIfDoesNotMatchesExpectation() {
        def request = new MockHttpServletRequest("GET", "/test")
        request.contentType = MediaType.APPLICATION_JSON_VALUE
        assert !expectation.verify(new ClientRequest(request))
    }

    @Test
    void shouldNotVerifyIfNoContentTypeIsSet() {
        def request = new MockHttpServletRequest("GET", "/test")
        request.contentType = null
        assert !expectation.verify(new ClientRequest(request))
    }

}
