package com.confluex.mule.test.http.matchers

import javax.servlet.http.HttpServletRequest

class HttpRequestMatcher {

    private Closure matcher

    public HttpRequestMatcher(Closure closure) {
        this.matcher = closure
    }

    boolean matches(HttpServletRequest request) {
        matcher(request)
    }
}
