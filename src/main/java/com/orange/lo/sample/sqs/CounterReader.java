/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.sqs;

import com.orange.lo.sample.sqs.utils.Counters;
import io.micrometer.core.instrument.Counter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@EnableScheduling
@Component
public class CounterReader {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private Counter mqttRead;
    private Counter evtAttempt;
    private Counter evtOK;
    private Counter evtKO;
    private Counter evtAborted;
    private Counter evtRetried;
    private Counter evtRejected;
    private long oldEvtOk;

    public CounterReader(Counters counterProvider) {
        mqttRead = counterProvider.mqttEvents();
        evtAttempt = counterProvider.evtAttemptCount();
        evtKO = counterProvider.evtFailure();
        evtOK = counterProvider.evtSuccess();
        evtAborted = counterProvider.evtAborted();
        evtRetried = counterProvider.evtRetried();
        evtRejected = counterProvider.evtRejected();
        oldEvtOk = 0;
    }

    @Scheduled(fixedRate = 1000)
    public void dumpCnt() {
        long rate = val(evtOK) - oldEvtOk;
        oldEvtOk = val(evtOK);
        if (rate > 0) {
            LOG.info("Mqtt received: {}, EvtSend attempted/rejected: {}/{}", val(mqttRead), val(evtAttempt), val(evtRejected));  //EvtSend can be either an attempt or reject
            LOG.info("Attempted EvtSend OK/abort: {}/{}, Evt KO: {}, Evt retries: {}", val(evtOK), val(evtAborted), val(evtKO), val(evtRetried)); //EvtSend attempt can be either OK or abort
            LOG.info("Rate: {} [msgs/s]", rate);
        }
    }

    private long val(Counter cnt) {
        return Math.round(cnt.count());
    }

}
