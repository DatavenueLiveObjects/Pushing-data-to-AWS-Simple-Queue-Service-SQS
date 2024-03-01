/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.liveobjects;

import java.lang.invoke.MethodHandles;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.orange.lo.sample.sqs.utils.Counters;
import com.orange.lo.sdk.fifomqtt.DataManagementFifoCallback;

import io.micrometer.core.instrument.Counter;

@Component
public class LoMqttHandler implements DataManagementFifoCallback {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Counter mesasageReadCounter;
    private final Queue<LoMessage> messageQueue;

    public LoMqttHandler(Counters counterProvider, Queue<LoMessage> messageQueue) {
        LOG.info("LoMqttHandler init...");

        this.mesasageReadCounter = counterProvider.getMesasageReadCounter();
        this.messageQueue = messageQueue;
    }

    @Override
    public void onMessage(int messageId, String message) {
        mesasageReadCounter.increment();
        messageQueue.add(new LoMessage(messageId, message));
    }
}