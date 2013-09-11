package com.confluex.mock.http.matchers

import com.confluex.mock.http.ClientRequest
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.xml.sax.SAXException

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathFactory

class HttpMatchers {
    static HttpRequestMatcher anyRequest() {
        new HttpRequestMatcher({
            return true // matches everything
        })
    }

    static HttpRequestMatcher path(String requestPath) {
        path(Matchers.equalTo(requestPath))
    }

    static HttpRequestMatcher path(Matcher<String> pathMatcher) {
        new HttpRequestMatcher({ ClientRequest request ->
            return pathMatcher.matches(request.path)
        })
    }

    static HttpRequestMatcher method(Matcher<String> methodMatcher) {
        new HttpRequestMatcher({ ClientRequest request ->
            return methodMatcher.matches(request.method)
        })
    }

    static HttpRequestMatcher method(String requestMethod) {
        method(Matchers.equalTo(requestMethod))
    }

    static HttpRequestMatcher get() {
        method('GET')
    }

    static HttpRequestMatcher get(String requestPath) {
        get().and(path(requestPath))
    }

    static HttpRequestMatcher get(Matcher<String> pathMatcher) {
        get().and(path(pathMatcher))
    }

    static HttpRequestMatcher put() {
        method('PUT')
    }

    static HttpRequestMatcher put(String requestPath) {
        put().and(path(requestPath))
    }

    static HttpRequestMatcher put(Matcher<String> pathMatcher) {
        put().and(path(pathMatcher))
    }

    static HttpRequestMatcher post() {
        method('POST')
    }

    static HttpRequestMatcher post(String requestPath) {
        post().and(path(requestPath))
    }

    static HttpRequestMatcher post(Matcher<String> pathMatcher) {
        post().and(path(pathMatcher))
    }

    static HttpRequestMatcher delete() {
        method('DELETE')
    }

    static HttpRequestMatcher delete(String requestPath) {
        delete().and(path(requestPath))
    }

    static HttpRequestMatcher delete(Matcher<String> pathMatcher) {
        delete().and(path(pathMatcher))
    }

    static HttpRequestMatcher body(String requestBody) {
        return body(Matchers.equalTo(requestBody))
    }

    static HttpRequestMatcher body(Matcher<String> bodyMatcher) {
        new HttpRequestMatcher({ ClientRequest request ->
            return bodyMatcher.matches(request.body)
        })
    }

    static HttpRequestMatcher queryParam(String key) {
        new HttpRequestMatcher({ ClientRequest request ->
            return request.queryParams.containsKey(key)
        })
    }

    static HttpRequestMatcher queryParam(String key, String value) {
        queryParam(key, Matchers.equalTo(value))
    }

    static HttpRequestMatcher queryParam(String key, Matcher<String> valueMatcher) {
        new HttpRequestMatcher({ ClientRequest request ->
            return valueMatcher.matches(request.queryParams[key])
        })
    }

    static HttpRequestMatcher header(String key) {
        new HttpRequestMatcher({ ClientRequest request ->
            return request.headers.containsKey(key)
        })
    }

    static HttpRequestMatcher header(String key, String value) {
        header(key, Matchers.equalTo(value))
    }

    static HttpRequestMatcher header(String key, Matcher<String> valueMatcher) {
        new HttpRequestMatcher({ ClientRequest request ->
            return valueMatcher.matches(request.headers[key])
        })
    }

    static Matcher<String> matchesPattern(final String regex) {
        new BaseMatcher<String>() {
            @Override
            boolean matches(Object o) {
                return o ==~ regex
            }

            @Override
            void describeTo(Description description) {
                description.appendText("a string matching the regular expression ")
                    .appendValue(regex)
            }
        }
    }

    static Matcher<String> hasXPath(final String xpath) {
        hasXPath(xpath, Matchers.any(String))
    }

    static Matcher<String> hasXPath(final String xpath, final Matcher<String> valueMatcher) {
        final def evaluator = XPathFactory.newInstance().newXPath()
        new BaseMatcher<String>() {

            @Override
            boolean matches(Object o) {
                def builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                try {
                    def doc = builder.parse(new ByteArrayInputStream(o.bytes)).documentElement
                    return evaluator.evaluate(xpath, doc)
                }
                catch (SAXException e) {
                    return false
                }
            }

            @Override
            void describeTo(Description description) {
                Matchers.hasXPath(xpath, valueMatcher).describeTo(description)
            }
        }
    }
}
