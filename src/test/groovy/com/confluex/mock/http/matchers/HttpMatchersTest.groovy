package com.confluex.mock.http.matchers

import com.confluex.mock.http.ClientRequest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;


@RunWith(MockitoJUnitRunner)
class HttpMatchersTest {

    @Mock
    ClientRequest request

    @Test
    void anyRequestShouldReturnTrueAlways() {
        assert HttpMatchers.anyRequest().matches(request)
    }

    @Test
    void pathShouldMatchRequestPathInfo() {
        when(request.getPath()).thenReturn('/wp-admin/post.php')
        assert HttpMatchers.path('/wp-admin/post.php').matches(request)
        assert ! HttpMatchers.path('/wp-admin/edit.php').matches(request)
    }

    @Test
    void pathShouldUseHamcrestMatcher() {
        when(request.getPath()).thenReturn('/wp-admin/post.php')
        assert HttpMatchers.path(endsWith('.php')).matches(request)
        assert ! HttpMatchers.path(containsString('edit')).matches(request)
    }

    @Test
    void bodyShouldMatchRequestBody() {
        when(request.getBody()).thenReturn('firstname=bender&lastname=rodriguez')
        assert HttpMatchers.body('firstname=bender&lastname=rodriguez').matches(request)
        assert ! HttpMatchers.body('firstname=philip&lastname=fry').matches(request)
    }

    @Test
    void bodyShouldUseHamcrestMatcher() {
        when(request.getBody()).thenReturn('firstname=bender&lastname=rodriguez')
        assert HttpMatchers.body(containsString('bender')).matches(request)
        assert ! HttpMatchers.body(not(containsString('bender'))).matches(request)
    }

    @Test
    void queryParamShouldMatchRequestQueryParameterInformation() {
        when(request.getQueryParams()).thenReturn([firstName: 'Bender', lastName: 'Rodriguez'])
        assert HttpMatchers.queryParam('firstName').matches(request)
        assert ! HttpMatchers.queryParam('lastName', 'Fry').matches(request)
        assert HttpMatchers.queryParam('lastName', 'Rodriguez').matches(request)
        assert ! HttpMatchers.queryParam('imaginaryName', 'Homer').matches(request)
    }

    @Test
    void queryParamShouldUseHamcrestMatcher() {
        when(request.getQueryParams()).thenReturn([firstName: 'Bender', lastName: 'Rodriguez'])
        assert HttpMatchers.queryParam('firstName', startsWith('B')).matches(request)
        assert ! HttpMatchers.queryParam('lastName', startsWith('B')).matches(request)
    }

    @Test
    void headerShouldMatchRequestHeader() {
        when(request.getHeaders()).thenReturn(['Content-Type': 'application/xml', 'Accept': 'text/html'])
        assert HttpMatchers.header('Accept').matches(request)
        assert ! HttpMatchers.header('Connection').matches(request)
        assert HttpMatchers.header('Content-Type', 'application/xml').matches(request)
        assert ! HttpMatchers.header('Content-Type', 'text/html').matches(request)
    }

    @Test
    void headerShouldUseHamcrestMatcher() {
        when(request.getHeaders()).thenReturn(['Content-Type': 'application/xml', 'Accept': 'text/html'])
        assert HttpMatchers.header('Content-Type', startsWith('application')).matches(request)
        assert ! HttpMatchers.header('Content-Type', startsWith('text')).matches(request)
    }

    @Test
    void methodShouldMatchRequestMethod() {
        when(request.getMethod()).thenReturn('ASSASSINATE')

        assert ! HttpMatchers.method('GET').matches(request)
        assert ! HttpMatchers.method('PUT').matches(request)
        assert ! HttpMatchers.method('POST').matches(request)
        assert ! HttpMatchers.method('DELETE').matches(request)
        assert HttpMatchers.method('ASSASSINATE').matches(request)
        assert HttpMatchers.method(containsString('ASSASSIN')).matches(request)
    }

    @Test
    void getShouldMatchGetMethodAndPath() {
        when(request.getMethod()).thenReturn('GET')
        when(request.getPath()).thenReturn('/wp-admin/post.php')

        assert HttpMatchers.get('/wp-admin/post.php').matches(request)
        assert ! HttpMatchers.get('/wp-admin/edit.php').matches(request)

        when(request.getMethod()).thenReturn('POST')

        assert ! HttpMatchers.get('/wp-admin/post.php').matches(request)
    }

    @Test
    void putShouldMatchPutMethodAndPath() {
        when(request.getMethod()).thenReturn('PUT')
        when(request.getPath()).thenReturn('/wp-admin/post.php')

        assert HttpMatchers.put('/wp-admin/post.php').matches(request)
        assert ! HttpMatchers.put('/wp-admin/edit.php').matches(request)

        when(request.getMethod()).thenReturn('POST')

        assert ! HttpMatchers.put('/wp-admin/post.php').matches(request)
    }

    @Test
    void postShouldMatchPostMethodAndPath() {
        when(request.getMethod()).thenReturn('POST')
        when(request.getPath()).thenReturn('/wp-admin/post.php')

        assert HttpMatchers.post('/wp-admin/post.php').matches(request)
        assert ! HttpMatchers.post('/wp-admin/edit.php').matches(request)

        when(request.getMethod()).thenReturn('GET')

        assert ! HttpMatchers.post('/wp-admin/post.php').matches(request)
    }

    @Test
    void deleteShouldMatchDeleteMethodAndPath() {
        when(request.getMethod()).thenReturn('DELETE')
        when(request.getPath()).thenReturn('/wp-admin/post.php')

        assert HttpMatchers.delete('/wp-admin/post.php').matches(request)
        assert ! HttpMatchers.delete('/wp-admin/edit.php').matches(request)

        when(request.getMethod()).thenReturn('POST')

        assert ! HttpMatchers.delete('/wp-admin/post.php').matches(request)
    }

    @Test
    void stringHasXpath() {
        String xml = '<feed><title>Interesting Posts</title><entry><title>Where Dark Energy Comes From</title></entry></feed>'
        assert HttpMatchers.stringHasXPath('/feed/entry/title').matches(xml)
        assert ! HttpMatchers.stringHasXPath('/feed/subtitle').matches(xml)
        assert HttpMatchers.stringHasXPath('/feed/entry/title', containsString('Dark Energy')).matches(xml)
        assert ! HttpMatchers.stringHasXPath('/feed/entry').matches('{ "docType": "json" }')
    }

    @Test
    void headShouldMatchHeadMethodAndPath() {
        when(request.getMethod()).thenReturn('HEAD')
        when(request.getPath()).thenReturn('/wp-admin/post.php')

        assert HttpMatchers.head('/wp-admin/post.php').matches(request)
        assert ! HttpMatchers.head('/wp-admin/edit.php').matches(request)

        when(request.getMethod()).thenReturn('POST')

        assert ! HttpMatchers.head('/wp-admin/post.php').matches(request)
    }

    @Test
    void optionsShouldMatchOptionsMethodAndPath() {
        when(request.getMethod()).thenReturn('OPTIONS')
        when(request.getPath()).thenReturn('/wp-admin/post.php')

        assert HttpMatchers.options('/wp-admin/post.php').matches(request)
        assert ! HttpMatchers.options('/wp-admin/edit.php').matches(request)

        when(request.getMethod()).thenReturn('POST')

        assert ! HttpMatchers.options('/wp-admin/post.php').matches(request)
    }
}
