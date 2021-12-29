/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.liveobjects;


import com.google.common.collect.Lists;
import com.orange.lo.sample.sqs.sqs.SqsSender;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Queue;

@Component
public class LoService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final SqsSender sqsSender;
    private final Queue<String> messageQueue;
    private final DataManagementFifo dataManagementFifo;
    private final LoProperties loProperties;
    private static final int DEFAULT_BATCH_SIZE = 10;

    public LoService(LOApiClient loApiClient, SqsSender sqsSender, Queue<String> messageQueue, LoProperties loProperties) {
        LOG.info("LoService init...");

        this.sqsSender = sqsSender;
        this.messageQueue = messageQueue;
        this.dataManagementFifo = loApiClient.getDataManagementFifo();
        this.loProperties = loProperties;
    }

    public void start() {
        dataManagementFifo.connectAndSubscribe();
    }

    public void stop() {
        dataManagementFifo.disconnect();
    }

    @Scheduled(fixedRateString = "${lo.synchronization-interval}")
    public void send() {
        if (!messageQueue.isEmpty()) {
            LOG.info("Start retriving messages...");

            int batchSize = loProperties.getMessageBatchSize() != null ? loProperties.getMessageBatchSize() : DEFAULT_BATCH_SIZE;

            List<String> messageBatch = Lists.newArrayListWithCapacity(batchSize);
            while (!messageQueue.isEmpty()) {
                messageBatch.add(messageQueue.poll());
                if (messageBatch.size() == batchSize) {
                    sqsSender.send(Lists.newArrayList(messageBatch));
                    messageBatch.clear();
                }
            }

            if (!messageBatch.isEmpty())
                sqsSender.send(Lists.newArrayList(messageBatch));
        }
    }
}