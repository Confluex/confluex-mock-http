package com.confluex.mule.test.http.matchers

import javax.servlet.http.HttpServletRequest

class HttpMatchers {
    static HttpRequestMatcher anyRequest() {
        new HttpRequestMatcher({
            return true // matches everything
        })
    }

    static HttpRequestMatcher path(String path) {
        new HttpRequestMatcher({ HttpServletRequest request ->
            return path == request.getPathInfo()
        })
    }
}
