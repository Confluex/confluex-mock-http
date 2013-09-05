package com.confluex.mock.http.expectations

import com.confluex.mock.http.ClientRequest

interface Expectation {
    /**
     * Inspect the request and decide if it is valid
     *
     * @param request captured resutls from http request
     * @return true if request is valid
     */
    Boolean verify(ClientRequest request)

}
