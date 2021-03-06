package com.confluex.mock.http

import groovy.transform.ToString

import javax.servlet.http.HttpServletRequest

@ToString(includeNames=true)
class ClientRequest {
    Map<String, String> headers = [:]
    Map<String, String> queryParams = [:]
    String contentType
    String body
    String method
    String url
    String path
    String queryString

    ClientRequest(HttpServletRequest request) {
        this.method = request.method
        this.contentType = request.contentType
        this.body = request.inputStream?.text
        this.url = request.requestURL.toString()
        this.path = request.pathInfo
        this.queryString = request.queryString
        request.headerNames.each { String name ->
            headers[name] = request.getHeader(name)
        }
        this.queryString?.split('&').each { queryParam ->
            def tuple = queryParam.split('=').collect() { URLDecoder.decode(it) }
            queryParams[tuple[0]] = tuple[1]
        }
    }
}
