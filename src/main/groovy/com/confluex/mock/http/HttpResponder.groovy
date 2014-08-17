package com.confluex.mock.http

import groovy.util.logging.Slf4j
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

import javax.servlet.http.HttpServletResponse
import java.util.zip.GZIPOutputStream

import static javax.servlet.http.HttpServletResponse.*

@Slf4j
class HttpResponder {
    boolean asGzipped
    int status = SC_OK
    Resource body = new ByteArrayResource(''.bytes)
    Closure bodyClosure

    Map<String, String> headers = [:]

    void render(ClientRequest request, HttpServletResponse response) {
        response.status = status
        def body = (bodyClosure?.call(request) ?: body.inputStream.text) // this is a memory hog
        log.debug "Responding $status with headers $headers and body: $body"
        headers.each { k, v ->
            response.addHeader(k, v)
        }

        if (asGzipped) {
            def targetStream = new ByteArrayOutputStream()
            def zipStream = new GZIPOutputStream(targetStream)
            zipStream << body
            zipStream.close()

            body = targetStream.toByteArray()
        }

        response.outputStream << body
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

    HttpResponderBuilder asGzipped() {
        responder.headers['Content-Encoding'] = 'gzip'
        responder.asGzipped = true
        this
    }
}
