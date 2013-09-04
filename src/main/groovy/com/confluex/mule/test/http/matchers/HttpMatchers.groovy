package com.confluex.mule.test.http.matchers

import com.confluex.mule.test.http.ClientRequest
import org.hamcrest.Matcher
import org.hamcrest.Matchers

class HttpMatchers {
    static HttpRequestMatcher anyRequest() {
        new HttpRequestMatcher({
            return true // matches everything
        })
    }

    static HttpRequestMatcher path(String requestPath) {
        path(Matchers.equalTo(requestPath))
    }

    static HttpRequestMatcher path(Matcher<String> pathMatcher) {
        new HttpRequestMatcher({ ClientRequest request ->
            return pathMatcher.matches(request.path)
        })
    }

    static HttpRequestMatcher body(String requestBody) {
        return body(Matchers.equalTo(requestBody))
    }

    static HttpRequestMatcher body(Matcher<String> bodyMatcher) {
        new HttpRequestMatcher({ ClientRequest request ->
            return bodyMatcher.matches(request.body)
        })
    }

    static HttpRequestMatcher queryParam(String key) {
        new HttpRequestMatcher({ ClientRequest request ->
            return request.queryParams.containsKey(key)
        })
    }

    static HttpRequestMatcher queryParam(String key, String value) {
        queryParam(key, Matchers.equalTo(value))
    }

    static HttpRequestMatcher queryParam(String key, Matcher<String> valueMatcher) {
        new HttpRequestMatcher({ ClientRequest request ->
            return valueMatcher.matches(request.queryParams[key])
        })
    }

    static HttpRequestMatcher header(String key) {
        new HttpRequestMatcher({ ClientRequest request ->
            return request.headers.containsKey(key)
        })
    }

    static HttpRequestMatcher header(String key, String value) {
        header(key, Matchers.equalTo(value))
    }

    static HttpRequestMatcher header(String key, Matcher<String> valueMatcher) {
        new HttpRequestMatcher({ ClientRequest request ->
            return valueMatcher.matches(request.headers[key])
        })
    }
}
