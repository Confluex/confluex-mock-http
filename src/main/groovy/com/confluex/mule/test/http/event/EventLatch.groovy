package com.confluex.mule.test.http.event


interface EventLatch {
    Integer addEvent()
    Boolean waitForEvents(Integer expected, Long timeout)
}
