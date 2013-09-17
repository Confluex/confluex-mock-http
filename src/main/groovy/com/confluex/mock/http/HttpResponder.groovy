package com.confluex.mock.http

import groovy.util.logging.Slf4j
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

import javax.servlet.http.HttpServletResponse

import static javax.servlet.http.HttpServletResponse.*

@Slf4j
class HttpResponder {
    int status = SC_OK
    Resource body = new ByteArrayResource("".bytes)
    Closure bodyClosure

    Map<String, String> headers = [:]

    void render(ClientRequest request, HttpServletResponse response) {
        response.status = status
        def body = (bodyClosure?.call(request) ?: body.inputStream.text) // this is a memory hog
        log.debug "Responding $status with body: $body"
        response.outputStream << body
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

    HttpResponderBuilder withBody(Closure closure) {
        responder.bodyClosure = closure
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
