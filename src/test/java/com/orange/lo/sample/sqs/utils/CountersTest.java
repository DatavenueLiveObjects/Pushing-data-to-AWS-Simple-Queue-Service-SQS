package com.orange.lo.sample.sqs.utils;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.noop.NoopCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountersTest {

    @Mock
    private MeterRegistry registry;
    private Counter mqttReadStub;
    private Counter evtAttemptStub;
    private Counter evtOKStub;
    private Counter evtKOStub;
    private Counter evtAbortedStub;
    private Counter evtRetriedStub;
    private Counter evtRejectedStub;
    private Counters counters;

    @BeforeEach
    void setUp() {
        mqttReadStub = getCounter("mqtt-received");
        when(registry.counter("mqtt-received")).thenReturn(mqttReadStub);
        evtAttemptStub = getCounter("evt-attempt");
        when(registry.counter("evt-attempt")).thenReturn(evtAttemptStub);
        evtOKStub = getCounter("evt-sent");
        when(registry.counter("evt-sent")).thenReturn(evtOKStub);
        evtKOStub = getCounter("evt-failed");
        when(registry.counter("evt-failed")).thenReturn(evtKOStub);
        evtAbortedStub = getCounter("evt-aborted");
        when(registry.counter("evt-aborted")).thenReturn(evtAbortedStub);
        evtRetriedStub = getCounter("evt-retried");
        when(registry.counter("evt-retried")).thenReturn(evtRetriedStub);
        evtRejectedStub = getCounter("evt-rejected");
        when(registry.counter("evt-rejected")).thenReturn(evtRejectedStub);

        this.counters = new Counters(registry);
    }

    @Test
    void shouldReturnCorrectCounterWhenEventSuccessCounterIsExpected() {
        Counter counter = counters.evtSuccess();
        assertEquals(evtOKStub, counter);
    }

    @Test
    void shouldReturnCorrectCounterWhenEventAbortedCounterIsExpected() {
        Counter counter = counters.evtAborted();
        assertEquals(evtAbortedStub, counter);
    }

    @Test
    void shouldReturnCorrectCounterWhenEventAttemptCounterIsExpected() {
        Counter counter = counters.evtAttemptCount();
        assertEquals(evtAttemptStub, counter);
    }

    @Test
    void shouldReturnCorrectCounterWhenEventFailedCounterIsExpected() {
        Counter counter = counters.evtFailure();
        assertEquals(evtKOStub, counter);
    }

    @Test
    void shouldReturnCorrectCounterWhenEventRejectedCounterIsExpected() {
        Counter counter = counters.evtRejected();
        assertEquals(evtRejectedStub, counter);
    }

    @Test
    void shouldReturnCorrectCounterWhenEventRetriedCounterIsExpected() {
        Counter counter = counters.evtRetried();
        assertEquals(evtRetriedStub, counter);
    }

    @Test
    void shouldReturnCorrectCounterWhenEventMqttCounterIsExpected() {
        Counter counter = counters.mqttEvents();
        assertEquals(mqttReadStub, counter);
    }

    private Counter getCounter(String name) {
        Tags tags = Tags.of(new ArrayList<>());
        Meter.Id id = new Meter.Id(name, tags, null, null, Meter.Type.COUNTER);
        return new NoopCounter(id);
    }
}