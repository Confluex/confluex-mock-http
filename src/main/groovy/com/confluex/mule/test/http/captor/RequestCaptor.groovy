package com.confluex.mule.test.http.captor

import com.confluex.mule.test.http.ClientRequest
import org.springframework.core.io.Resource

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


interface RequestCaptor {

    String getText()
    void setText(String val)

    Integer getStatus()
    void setStatus(Integer code)

    Resource getResource()
    void setResource(Resource resource)

    List<ClientRequest> getRequests()
    Map<String, String> getHeaders()

    void render(HttpServletRequest request, HttpServletResponse response)
}
