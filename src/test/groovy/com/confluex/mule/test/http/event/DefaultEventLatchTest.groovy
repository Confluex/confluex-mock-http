package com.confluex.mule.test.http.event

import groovy.util.logging.Slf4j
import org.junit.Before
import org.junit.Test


@Slf4j
class DefaultEventLatchTest {

    DefaultEventLatch latch

    @Before
    void createLatch() {
        latch = new DefaultEventLatch()
    }

    @Test
    void shouldCreateLatchWithProperCountWhenZeroEventsHaveOccurred() {
        latch.createLatchFromWithDeltaFromCurrentCount(10)
        assert latch.target.count == 10
    }

    @Test
    void shouldCreateLatchWithProperCountWhenEventsHaveAlreadyOccurred() {
        latch.addEvent()
        latch.addEvent()
        latch.addEvent()
        latch.createLatchFromWithDeltaFromCurrentCount(10)
        assert latch.target.count == 7
    }


    @Test
    void shouldWaitForCorrectNumberOfMessages() {
        5.times {
            Thread.start {
                log.info("Thread started to add event")
                latch.addEvent()
            }
        }
        assert latch.waitForEvents(5, 1000)
    }

    @Test
    void shouldReturnFalseIfExpectedNumberOfEventsDoNotOccurr() {
        5.times {
            Thread.start {
                log.info("Thread started to add event")
                latch.addEvent()
            }
        }
        assert !latch.waitForEvents(6, 1000)
    }
}
