package com.confluex.mule.test.http.expectations

import com.confluex.mule.test.http.ClientRequest
import groovy.transform.ToString
import org.springframework.http.MediaType

@ToString(includeNames = true)
class MediaTypeExpectation implements Expectation {


    public static final MediaTypeExpectation JSON = new MediaTypeExpectation(MediaType.APPLICATION_JSON_VALUE)
    public static final MediaTypeExpectation XML = new MediaTypeExpectation(MediaType.APPLICATION_XML_VALUE)
    public static final MediaTypeExpectation HTML = new MediaTypeExpectation(MediaType.TEXT_HTML_VALUE)
    public static final MediaTypeExpectation TEXT = new MediaTypeExpectation(MediaType.TEXT_PLAIN_VALUE)
    public static final MediaTypeExpectation FORM = new MediaTypeExpectation(MediaType.APPLICATION_FORM_URLENCODED_VALUE)

    String mediaType


    MediaTypeExpectation(String mediaType) {
        this.mediaType = mediaType.toLowerCase()
    }

    Boolean verify(ClientRequest request) {
        if (!request.contentType) {
            return false
        }
        return MediaType.parseMediaType(request.contentType).isCompatibleWith(MediaType.parseMediaType(this.mediaType))
    }

}
