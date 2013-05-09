[![Build Status](https://travis-ci.org/Confluex/confluex-mock-http.png?branch=master)](https://travis-ci.org/Confluex/confluex-mock-http)

# Confluex Mock HTTP API

Mock HTTP testing library for stubbing HTTP responses from text or resources on the classpath (xml files, etc.) and
verification of client behavior and state.

This library is still under heavy development. Feel free to use, contribute but there could be changes to
the API until it reaches 1.0 status. Of course, we'll try to keep breaking changes to a minimum.

**Table of Contents**

* [Example Usage] (#example-usage)
* [Maven Information] (#maven-information)
* [License] (#License)

**Groovy Examples**

Most of the examples documented here are using Groovy instead of Java. Feel free to use Java if you wish. There is
no Groovy requirement (Groovy is great and you should really check it out though!).

## Example Usage

The following demonstrates a Junit test case which creates an embedded Jetty server and uses the MockHttpRequestHandler
to setup mock responses to HTTP calls from clients and verification of the interaction with the server.

_Junit Test Case_

```groovy

package com.confluex.mule.test.http.jetty

import com.confluex.mule.test.http.MockHttpRequestHandler
import com.confluex.mule.test.http.expectations.HeaderExpectation
import com.confluex.mule.test.http.expectations.MediaTypeExpectation
import com.confluex.mule.test.http.expectations.MethodExpectation
import com.sun.jersey.api.client.Client
import groovy.util.logging.Slf4j
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mortbay.jetty.Server

import javax.ws.rs.core.MediaType

import static javax.servlet.http.HttpServletResponse.*

@Slf4j
class MockJettyHttpServerIntegrationTest {

    Server server

    /**
     * Create an embedded Jetty server for each test on port 9001
     */
    @Before
    void createServer() {
        server = new Server(9001)
        server.start()
    }

    /**
     * Stop the Jetty Server
     */
    @After
    void stopServer() {
        server.stop()
    }

    @Test
    void shouldServeSimpleContent() {

        // create the handler and setup the response data for the clients
        def handler = new MockHttpRequestHandler()
                .when("/foo")
                .thenReturnResource("/http/responses/foo.xml")
                .when("/bar")
                .thenReturnResource("/http/responses/bar.json")
                .withStatus(SC_OK)
                .withHeader("x-bender", "Who are you, and why should I care?")
                .withHeader("x-fry", "I did do the nasty in the pasty")

        // assign the handler to the Jetty Server
        server.handler = handler

        // make some HTTP requests. This is using the Jersey rest client. You'll likely be testing your
        // own internal code instead.
        2.times {
            def xml = Client.create().resource("http://localhost:9001/foo")
                    .accept(MediaType.APPLICATION_XML)
                    .type(MediaType.APPLICATION_XML)
                    .get(String.class)
            assert xml == this.class.getResourceAsStream("/http/responses/foo.xml").text
        }
        3.times {
            def json = Client.create().resource("http://localhost:9001/bar")
                    .type(MediaType.APPLICATION_JSON)
                    .post(String.class, '{"count":1}')
            assert json == this.class.getResourceAsStream("/http/responses/bar.json").text
        }

        // use the built in verifications
        handler.verify("/foo", MethodExpectation.GET, MediaTypeExpectation.XML)
        handler.verify("/bar",
                MethodExpectation.POST,
                MediaTypeExpectation.JSON,
                new HeaderExpectation("Host", "localhost:9001")
        )
        handler.verify("/random", MethodExpectation.PUT, MediaTypeExpectation.TEXT)

        // or grab the raw client request data and do your own assertions
        def requests = handler.getRequests("/foo")
        assert requests.size() == 2
        requests.each {
            assert it.contentType == "application/xml"
            assert it.headers["Host"] == "localhost:9001"
            assert it.headers["Accept"] == "application/xml"
        }
        assert handler.getRequests("/bar").size() == 3
    }
}

```



# Maven Information

Maven Artifact:

```xml
<dependency>
    <groupId>com.confluex</groupId>
    <artifactId>confluex-test-http</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

The artifacts are available in the [Maven Central Repository](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22confluex-mock-http%22).

# License

   Copyright 2013 Confluex, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
