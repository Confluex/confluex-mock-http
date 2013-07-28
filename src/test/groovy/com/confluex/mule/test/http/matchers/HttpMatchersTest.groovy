package com.confluex.mule.test.http.matchers

import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest

class HttpMatchersTest {

    MockHttpServletRequest request

    @Before
    void init() {
        request = new MockHttpServletRequest()
    }

    @Test
    void anyRequestShouldReturnTrueAlways() {
        assert HttpMatchers.anyRequest().matches(request)
    }

    @Test
    void pathShouldMatchRequestPathInfo() {
        request.setPathInfo('/wp-admin/post.php')
        assert HttpMatchers.path('/wp-admin/post.php').matches(request)
        assert ! HttpMatchers.path('/wp-admin/edit.php').matches(request)
    }
}
