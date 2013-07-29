package com.confluex.mule.test.http.event

import com.confluex.mule.test.http.ClientRequest
import com.confluex.mule.test.http.matchers.HttpRequestMatcher
import groovy.util.logging.Slf4j

import javax.servlet.http.HttpServletRequest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@Slf4j
class MatchingEventLatch {
    HttpRequestMatcher matcher
    protected final AtomicInteger currentCount = new AtomicInteger(0)
    CountDownLatch target

    MatchingEventLatch(HttpRequestMatcher matcher) {
        this(matcher, 1)
    }

    MatchingEventLatch(HttpRequestMatcher matcher, int expected) {
        this.matcher = matcher
        target = new CountDownLatch(expected)
    }

    Integer addEvent(ClientRequest request) {
        if (matcher.matches(request)) {
            def value = currentCount.incrementAndGet().intValue()
            target.countDown()
            log.debug("Latch matched an event")
            return value
        }
        log.debug("Latch received an event but did not match")
        return currentCount.intValue();
    }

    Boolean await(Long timeoutMs) {
        boolean finished = target.await(timeoutMs, TimeUnit.MILLISECONDS)
        log.info("Finished waiting on events. response: ${finished}, currentCount=${currentCount}, remaining=${target.count}")
        return finished
    }
}
