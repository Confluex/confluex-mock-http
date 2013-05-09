package com.confluex.mule.test.http.captor

import com.confluex.mule.test.http.ClientRequest
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import org.springframework.core.io.Resource

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@EqualsAndHashCode(excludes = "requests")
@ToString(includeNames = true, excludes = "requests")
@Slf4j
class DefaultRequestCaptor implements RequestCaptor {
    Map<String, String> headers = [:]
    List<ClientRequest> requests = []

    Resource resource
    Integer status = 200
    String text = "ok"


    void render(HttpServletRequest request, HttpServletResponse response) {
        log.info("Rendering response to client with captor {}", this)
        response.status = status
        headers.each { k, v ->
            response.addHeader(k, v)
        }

        // Not sure why this isn't working..
        //        response.outputStream << resource?.inputStream ?: text
        if (resource) {
            response.outputStream << resource.inputStream
        } else {
            response.outputStream << text
        }

        requests << new ClientRequest(request)
    }

}
