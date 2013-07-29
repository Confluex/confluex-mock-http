package com.confluex.mule.test.http.matchers

import com.confluex.mule.test.http.ClientRequest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import org.springframework.mock.web.MockHttpServletRequest

import static org.mockito.Mockito.*

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
    void bodyShouldMatchRequestBody() {
        when(request.getBody()).thenReturn('firstname=bender&lastname=rodriguez')
        assert HttpMatchers.body('firstname=bender&lastname=rodriguez').matches(request)
        assert ! HttpMatchers.body('firstname=philip&lastname=fry').matches(request)
    }

    @Test
    void queryParamShouldMatchRequestQueryParameterInformation() {
        when(request.getQueryParams()).thenReturn([firstName: 'Bender', lastName: 'Rodriguez'])
        assert HttpMatchers.queryParam('firstName').matches(request)
        assert ! HttpMatchers.queryParam('lastName', 'Fry').matches(request)
        assert HttpMatchers.queryParam('lastName', 'Rodriguez').matches(request)
        assert ! HttpMatchers.queryParam('imaginaryName', 'Homer').matches(request)
    }
}
