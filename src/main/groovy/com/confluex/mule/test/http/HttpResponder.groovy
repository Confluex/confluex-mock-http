package com.confluex.mule.test.http

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

import javax.servlet.http.HttpServletResponse

import static javax.servlet.http.HttpServletResponse.*

class HttpResponder {
    int status = SC_OK
    Resource body = new ByteArrayResource("".bytes)

    void render(HttpServletResponse response) {
        response.status = status
        response.outputStream << body.inputStream
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
}
