package com.confluex.mule.test.http.matchers

import com.confluex.mule.test.http.ClientRequest

class HttpMatchers {
    static HttpRequestMatcher anyRequest() {
        new HttpRequestMatcher({
            return true // matches everything
        })
    }

    static HttpRequestMatcher path(String path) {
        new HttpRequestMatcher({ ClientRequest request ->
            return path == request.path
        })
    }

    static HttpRequestMatcher body(String body) {
        new HttpRequestMatcher({ ClientRequest request ->
            return body == request.body
        })
    }

    static HttpRequestMatcher queryParam(String key) {
        new HttpRequestMatcher({ ClientRequest request ->
            return request.queryParams.containsKey(key)
        })
    }

    static HttpRequestMatcher queryParam(String key, String value) {
        new HttpRequestMatcher({ ClientRequest request ->
            return request.queryParams[key] == value
        })
    }
}
