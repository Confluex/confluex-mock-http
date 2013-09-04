package com.confluex.mule.test.http.matchers

import com.confluex.mule.test.http.ClientRequest
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
}
