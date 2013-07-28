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

The following demonstrates a JUnit test case which sets up mock responses to HTTP calls from clients and verifies
the interaction with the server.  It uses the Jersey Client to make the HTTP requests; your code will

_Hello World_

```groovy

import com.confluex.mule.test.http.MockHttpServer
import static com.confluex.mule.test.http.matchers.HttpMatchers.*

MockHttpServer server = new MockHttpServer(8080)
server.respondTo(anyRequest()).withBody('Hello World!')

```

In order to make sure the HTTP client sent the requests you expected, you can find out what requests the server received"

_Asserting on request information_

```groovy

ClientRequest request = server.requests.find() { it.method == 'GET' && it.path == '/widget/inventory' }
assertNotNull(request)
assertEquals('application/json', request.header['Content-Type']

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
