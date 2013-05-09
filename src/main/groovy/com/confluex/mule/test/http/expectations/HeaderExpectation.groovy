package com.confluex.mule.test.http.expectations

import com.confluex.mule.test.http.ClientRequest
import groovy.transform.ToString

@ToString(includeNames = true)
class HeaderExpectation implements Expectation {

    String key
    String value

    HeaderExpectation(String key, String value) {
        this.key = key
        this.value = value
    }

    @Override
    Boolean verify(ClientRequest request) {
        return request.headers[key] == value
    }
}
