package com.confluex.mule.test.http

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

import javax.servlet.http.HttpServletResponse

import static javax.servlet.http.HttpServletResponse.*

class HttpResponder {
    int status = SC_OK
    Resource body = new ByteArrayResource("".bytes)
    Map<String, String> headers = [:]

    void render(HttpServletResponse response) {
        response.status = status
        response.outputStream << body.inputStream
        headers.each { k, v ->
            response.addHeader(k, v)
        }
    }
}

class HttpResponderBuilder {
    HttpResponder responder = new HttpResponder()

    HttpResponderBuilder withBody(String text) {
        responder.body = new ByteArrayResource(text.bytes)
        this
    }

    HttpResponderBuilder withResource(String path) {
        responder.body = new ClassPathResource(path)
        this
    }

    HttpResponderBuilder withStatus(int status) {
        responder.status = status
        this
    }

    HttpResponderBuilder withHeader(String name, String value) {
        responder.headers[name] = value
        this
    }
}
