package com.confluex.mule.test.http.matchers

import com.confluex.mule.test.http.ClientRequest

import javax.servlet.http.HttpServletRequest

class HttpRequestMatcher {

    private Closure matcher

    public HttpRequestMatcher(Closure closure) {
        this.matcher = closure
    }

    boolean matches(ClientRequest request) {
        matcher(request)
    }

    HttpRequestMatcher and(HttpRequestMatcher otherMatcher) {
        def left = this
        def right = otherMatcher
        new HttpRequestMatcher({ ClientRequest request ->
            left.matches(request) && right.matches(request)
        })
    }
}
