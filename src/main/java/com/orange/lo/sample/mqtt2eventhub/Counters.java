/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.mqtt2eventhub;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Counters {
    private Counter mqttRead;
    private Counter evtAttempt;
    private Counter evtOK;
    private Counter evtKO;
    private Counter evtAborted;
    private Counter evtRetried;
    private Counter evtRejected;

    @Autowired
    public Counters(MeterRegistry registry) {
        mqttRead = registry.counter("mqtt-received");
        evtAttempt = registry.counter("evt-attempt");
        evtOK = registry.counter("evt-sent");
        evtKO = registry.counter("evt-failed");
        evtAborted = registry.counter("evt-aborted");
        evtRetried = registry.counter("evt-retried");
        evtRejected = registry.counter("evt-rejected");
    }

    public Counter mqttEvents() {
        return mqttRead;
    }

    public Counter evtAttemptCount() {
        return evtAttempt;
    }

    public Counter evtSuccess() {
        return evtOK;
    }

    public Counter evtFailure() {
        return evtKO;
    }

    public Counter evtAborted() {
        return evtAborted;
    }

    public Counter evtRetried() {
        return evtRetried;
    }

    public Counter evtRejected() {
        return evtRejected;
    }
}
