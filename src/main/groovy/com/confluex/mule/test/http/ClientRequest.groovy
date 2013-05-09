package com.confluex.mule.test.http

import groovy.transform.ToString

import javax.servlet.http.HttpServletRequest

@ToString(includeNames=true)
class ClientRequest {
    Map<String, String> headers = [:]
    String contentType
    String body
    String method
    String url

    ClientRequest(HttpServletRequest request) {
        this.method = request.method
        this.contentType = request.contentType
        this.body = request.inputStream?.text
        this.url = request.requestURL.toString()
        request.headerNames.each { String name ->
            headers[name] = request.getHeader(name)
        }
    }
}
