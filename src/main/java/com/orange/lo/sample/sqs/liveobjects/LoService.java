/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.liveobjects;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.orange.lo.sample.sqs.sqs.SqsProperties;
import com.orange.lo.sample.sqs.sqs.SqsSender;
import com.orange.lo.sample.sqs.utils.ConnectorHealthActuatorEndpoint;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifo;
import com.orange.lo.sdk.mqtt.exceptions.LoMqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
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
    private final AmazonSQS amazonSQS;
    private final SqsProperties sqsProperties;
    private final ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint;

    public LoService(LOApiClient loApiClient, SqsSender sqsSender, Queue<String> messageQueue, LoProperties loProperties,
                     AmazonSQS amazonSQS, SqsProperties sqsProperties,
                     ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint) {
        LOG.info("LoService init...");

        this.sqsSender = sqsSender;
        this.messageQueue = messageQueue;
        this.dataManagementFifo = loApiClient.getDataManagementFifo();
        this.loProperties = loProperties;
        this.amazonSQS = amazonSQS;
        this.sqsProperties = sqsProperties;
        this.connectorHealthActuatorEndpoint = connectorHealthActuatorEndpoint;
    }

    @PostConstruct
    public void start() {
        try {
            amazonSQS.getQueueUrl(sqsProperties.getQueueUrl());
        } catch (AmazonSQSException e) {
            LOG.error("Problem with connection. Check AWS credentials. " + e.getErrorMessage(), e);
            connectorHealthActuatorEndpoint.setCloudConnectionStatus(false);
        } catch (Exception e) {
            LOG.error("Problem with connection. " + e.getMessage(), e);
            connectorHealthActuatorEndpoint.setCloudConnectionStatus(false);
        }

        try {
            dataManagementFifo.connect();
        } catch (LoMqttException e) {
            LOG.error("Problem with connection. Check Lo credentials. ", e);
            connectorHealthActuatorEndpoint.setLoConnectionStatus(false);
        }

        if (dataManagementFifo.isConnected())
            dataManagementFifo.disconnect();

        if (connectorHealthActuatorEndpoint.isCloudConnectionStatus() && connectorHealthActuatorEndpoint.isLoConnectionStatus())
            dataManagementFifo.connectAndSubscribe();
    }

    @PreDestroy
    public void stop() {
        dataManagementFifo.disconnect();
    }

    @Scheduled(fixedRateString = "${lo.synchronization-interval}")
    public void send() {
        if (!messageQueue.isEmpty()) {
            LOG.info("Start retriving messages...");

            int batchSize = loProperties.getMessageBatchSize() != null ? loProperties.getMessageBatchSize() : DEFAULT_BATCH_SIZE;

            List<String> messageBatch = new ArrayList<>(batchSize);
            while (!messageQueue.isEmpty()) {
                messageBatch.add(messageQueue.poll());
                if (messageBatch.size() == batchSize) {
                    sqsSender.send(new ArrayList<>(messageBatch));
                    messageBatch.clear();
                }
            }
            if (!messageBatch.isEmpty())
                sqsSender.send(new ArrayList<>(messageBatch));
        }
    }
}