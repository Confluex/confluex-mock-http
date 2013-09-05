package com.confluex.mock.http.expectations

import com.confluex.mock.http.ClientRequest
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest


class MethodExpectationTest {
    protected MethodExpectation expectation

    @Before
    void createExpectation() {
        expectation = MethodExpectation.GET
    }


    @Test
    void shouldVerifyIfHttpMethodMatchesExpectations() {
        def request = new MockHttpServletRequest("GET", "/test")
        assert expectation.verify(new ClientRequest(request))
        request = new MockHttpServletRequest("gEt", "/test")
        assert expectation.verify(new ClientRequest(request))
    }

    @Test
    void shouldNotVerifyIfHttpMethodDoesNotMatchExpectation() {
        def request = new MockHttpServletRequest("POST", "/test")
        assert !expectation.verify(new ClientRequest(request))
    }

}
