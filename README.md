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

_Hello World_

The simplest usage of this library is to create a MockHttpServer, and set it up to respond the same regardless of
what requests it receives.  Once you have instantiated the MockHttpServer, it is listening on localhost on the port
you provided.

```groovy

import com.confluex.mule.test.http.MockHttpServer
import static com.confluex.mule.test.http.matchers.HttpMatchers.*

MockHttpServer server = new MockHttpServer(8080)
server.respondTo(anyRequest()).withBody('Hello World!')

// ... clients can now connect to port 8080 and send HTTP requests.

server.stop()

```

_Handling different paths_

You can instruct the MockHttpServer to respond to specific paths.  When a requests does not match anything, the
server responds with a 404 status code.  When you use respondTo to match HTTP requests, the server responds with status
code 200 and an empty response body unless you instruct it to do otherwise.

```groovy

MockHttpServer server = new MockHttpServer(8080)
server.respondTo(path('/about-us')).withBody('We are awesome')
server.respondTo(path('/blog/create-post.php')).withStatus(201).withBody('Created')

```

_Doing more with the response_

You can control the response status, body, and headers

```groovy

server.respondTo(path('/not-found'))
    .withStatus(404)
    .withBody('Sorry, buddy')
    .withHeader('Content-Type', 'text/plain')
    .withHeader('Last-Modified', 'Tue, 15 Nov 1994 12:45:26 GMT')

```

_Asserting on request information_

In order to make sure the HTTP client sent the requests you expected, you can find out what requests the server received.

```groovy

ClientRequest request = server.requests.find() { it.method == 'GET' && it.path == '/widget/inventory' }
assertNotNull(request)
assertEquals('application/json', request.header['Content-Type']

```

_Finding an available port_

If you don't particularly care what port the HTTP server listens to, you can allow it to find an available port.

```groovy

MockHttpServer server = new MockHttpServer()
int thePortItChose = server.port

```

# Maven Information

Maven Artifact:

```xml
<dependency>
    <groupId>com.confluex</groupId>
    <artifactId>confluex-mock-http</artifactId>
    <version>0.1.0</version>
    <scope>test</scope>
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
