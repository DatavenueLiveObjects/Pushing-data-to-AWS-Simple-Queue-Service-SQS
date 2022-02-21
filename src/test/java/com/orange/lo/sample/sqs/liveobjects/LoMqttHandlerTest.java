/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.liveobjects;

import com.orange.lo.sample.sqs.utils.Counters;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoMqttHandlerTest {

    @Mock
    private Counters counterProvider;

    @Mock
    private Counter counter;

    private Queue<String> messageQueue;

    private LoMqttHandler handler;

    @BeforeEach
    void setUp() {
        when(counterProvider.getMesasageReadCounter()).thenReturn(counter);
        messageQueue = new LinkedList<>();
        handler = new LoMqttHandler(counterProvider, messageQueue);
    }

    @Test
    public void shouldIncrementCounterOnMessage() {
        // when
        handler.onMessage("test message");

        // then
        verify(counter, times(1)).increment();
    }

    @Test
    public void shouldAddMessageToQueueOnMessage() {
        // when
        handler.onMessage("test message");

        // then
        assertEquals(1, messageQueue.size());
        assertEquals("test message", messageQueue.peek());
    }

}