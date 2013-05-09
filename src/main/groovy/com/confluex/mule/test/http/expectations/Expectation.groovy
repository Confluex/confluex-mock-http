package com.confluex.mule.test.http.expectations

import com.confluex.mule.test.http.ClientRequest

interface Expectation {
    /**
     * Inspect the request and decide if it is valid
     *
     * @param request captured resutls from http request
     * @return true if request is valid
     */
    Boolean verify(ClientRequest request)

}
