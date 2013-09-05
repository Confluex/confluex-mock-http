package com.confluex.mock.http.expectations

import com.confluex.mock.http.ClientRequest
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest


class HeaderExpectationTest {

    HeaderExpectation expectation

    @Before
    void createExpectation() {
        expectation = new HeaderExpectation("a", "123")
    }

    @Test
    void shouldPassIfHeaderValueExists() {
        def request = new MockHttpServletRequest()
        request.addHeader("a", "123")
        assert expectation.verify(new ClientRequest(request))
    }

    @Test
    void shouldNotPassIfHeaderKeyDoesNotExists() {
        def request = new MockHttpServletRequest()
        request.addHeader("b", "123")
        assert !expectation.verify(new ClientRequest(request))
    }

    @Test
    void shouldNotPassIfHeaderValueIsWrong() {
        def request = new MockHttpServletRequest()
        request.addHeader("a", "456")
        assert !expectation.verify(new ClientRequest(request))
    }
}
