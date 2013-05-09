package com.confluex.mule.test.http.event

import groovy.transform.ToString
import groovy.util.logging.Slf4j

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@Slf4j
@ToString(includeNames = true)
class DefaultEventLatch implements EventLatch {
    protected final AtomicInteger currentCount = new AtomicInteger(0)
    CountDownLatch target

    Integer addEvent() {
        log.debug("Added event")
        def value = currentCount.incrementAndGet().intValue()
        target?.countDown()
        return value
    }

    Boolean waitForEvents(Integer expected, Long timeoutMs) {
        createLatchFromWithDeltaFromCurrentCount(expected)
        def finished = target.await(timeoutMs, TimeUnit.MILLISECONDS)
        log.info("Finished waiting on events. response: ${finished}, currentCount=${currentCount}, expectedCount=${expected}")
        return finished
    }

    void createLatchFromWithDeltaFromCurrentCount(Integer expected) {
        synchronized (currentCount) {
            target = new CountDownLatch(expected - currentCount.get())
            log.info("Created latch with ${target.count} events left. ${currentCount} events already recorded.")
        }
    }
}
