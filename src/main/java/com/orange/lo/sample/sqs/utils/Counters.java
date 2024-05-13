/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.utils;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class Counters {

	private final Counter mesasageReadCounter;
    private final Counter mesasageSentAttemptCounter;
    private final Counter mesasageSentAttemptFailedCounter;
    private final Counter mesasageSentCounter;
    private final Counter mesasageSentFailedCounter;
    
    public Counters(MeterRegistry meterRegistry) {
    	mesasageReadCounter = meterRegistry.counter("message.read");
        mesasageSentAttemptCounter = meterRegistry.counter("message.sent.attempt");
        mesasageSentAttemptFailedCounter = meterRegistry.counter("message.sent.attempt.failed");
        mesasageSentCounter = meterRegistry.counter("message.sent");
        mesasageSentFailedCounter = meterRegistry.counter("message.sent.failed");
    }

	public Counter getMesasageReadCounter() {
		return mesasageReadCounter;
	}

	public Counter getMesasageSentAttemptCounter() {
		return mesasageSentAttemptCounter;
	}

	public Counter getMesasageSentAttemptFailedCounter() {
		return mesasageSentAttemptFailedCounter;
	}

	public Counter getMesasageSentCounter() {
		return mesasageSentCounter;
	}

	public Counter getMesasageSentFailedCounter() {
		return mesasageSentFailedCounter;
	}
	
	public List<Counter> getAll() {
		return Arrays.asList(mesasageReadCounter, mesasageSentAttemptCounter, mesasageSentAttemptFailedCounter, mesasageSentCounter, mesasageSentFailedCounter);
	}
}