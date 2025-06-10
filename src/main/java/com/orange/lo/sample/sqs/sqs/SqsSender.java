/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.sqs;

import com.amazonaws.AmazonClientException;
import com.amazonaws.retry.RetryPolicy.RetryCondition;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.EmptyBatchRequestException;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.orange.lo.sample.sqs.liveobjects.LoMessage;
import com.orange.lo.sample.sqs.liveobjects.LoProperties;
import com.orange.lo.sample.sqs.utils.Counters;
import com.orange.lo.sdk.LOApiClient;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@EnableScheduling
@Component
public class SqsSender {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SqsProperties sqsProperties;
    private final LoProperties loProperties;
    private final LOApiClient loApiClient;
    private final AmazonSQS sqs;
    private final Counters counters;
    private final ThreadPoolExecutor tpe;
    private final Random random;
    private final RetryPolicy<Void> sendMessageRetryPolicy;
    private final RetryPolicy<Void> executeTaskRetryPolicy;
    private final RetryCondition amazonRetryCondition;

    public SqsSender(
            AmazonSQS amazonSQS,
            SqsProperties sqsProperties,
            LoProperties loProperties,
            ThreadPoolExecutor tpe,
            Counters counters,
            RetryPolicy<Void> sendMessageRetryPolicy,
            RetryPolicy<Void> executeTaskRetryPolicy,
            RetryCondition amazonRetryCondition,
            LOApiClient loApiClient
    ) {
        this.sqs = amazonSQS;
        this.sqsProperties = sqsProperties;
        this.loProperties = loProperties;
        this.loApiClient = loApiClient;
        this.random = new Random();
        this.tpe = tpe;
        this.counters = counters;
        this.sendMessageRetryPolicy = sendMessageRetryPolicy;
        this.executeTaskRetryPolicy = executeTaskRetryPolicy;
        this.amazonRetryCondition = amazonRetryCondition;
    }

    public void send(List<LoMessage> messages) {
        Failsafe.with(executeTaskRetryPolicy)
                .run(() -> tpe.submit(() -> Failsafe.with(sendMessageRetryPolicy)
                        .onFailure(test -> {
                            LOG.debug("Cannot send messages to SQS because of: {}. Retrying...", test.getFailure().getMessage());
                            counters.getMesasageSentFailedCounter().increment(messages.size());
                        })
                        .onSuccess(test -> {
                            LOG.debug("Messages were sent to SQS");
                            counters.getMesasageSentCounter().increment(messages.size());
                        })
                        .onComplete(test -> {
                            LOG.debug("Acking messages");
                            messages.forEach(m -> loApiClient.getDataManagementFifo().sendAck(m.getMessageId(), loProperties.getMessageQos()));
                        })
                        .run(executionContext -> {
                            LOG.debug("Sending messages to SQS");
                            counters.getMesasageSentAttemptCounter().increment(messages.size());
                            sendBatches(messages.stream().map(LoMessage::getMessage).collect(Collectors.toList()), executionContext.getAttemptCount());
                        })));
    }

    private void sendBatches(List<String> batch, int attemptCount) throws RetryableAmazonClientException {
        if (attemptCount > 0) {
            LOG.info("Retrying to send ({}): {}", attemptCount, batch.hashCode());
        }

        AtomicInteger index = new AtomicInteger();

        List<SendMessageBatchRequestEntry> entries = batch.stream()
                .map(m -> toSendMessageBatchRequestEntry(index.getAndIncrement(), m, random.nextDouble()))
                .collect(Collectors.toList());

        SendMessageBatchRequest sendBatchRequest = new SendMessageBatchRequest()
                .withQueueUrl(sqsProperties.getQueueUrl())
                .withEntries(entries);

        try {
            sqs.sendMessageBatch(sendBatchRequest);
            counters.getCloudConnectionStatus().set(1);
        } catch (final AmazonClientException ace) {
            LOG.error("Problem with connection. {}", ace.getMessage(), ace);
            counters.getCloudConnectionStatus().set(0);
            counters.getMesasageSentAttemptFailedCounter().increment(sendBatchRequest.getEntries().size());
            boolean shouldRetry = amazonRetryCondition.shouldRetry(sendBatchRequest, ace, attemptCount);
            throw new RetryableAmazonClientException(ace, shouldRetry);
        }
    }

    private SendMessageBatchRequestEntry toSendMessageBatchRequestEntry(int id, String message, double deduplicationId) {
        return new SendMessageBatchRequestEntry(String.valueOf(id), message)
                .withMessageGroupId(sqsProperties.getMessageGroupId())
                .withMessageDeduplicationId(Double.toString(deduplicationId));
    }

    //TODO usunac
    @Scheduled(fixedRate = 30000)
    public void reportExecutorData() {
        LOG.info("Pool size: {}, active threads: {}, tasks in queue: {}", tpe.getPoolSize(), tpe.getActiveCount(), tpe.getQueue().size());
    }

    @PostConstruct
    public void checkConnection() {
        SendMessageBatchRequest sendBatchRequest = new SendMessageBatchRequest()
                .withQueueUrl(sqsProperties.getQueueUrl());
        try {
            sqs.sendMessageBatch(sendBatchRequest);
        } catch (EmptyBatchRequestException ignored) {
            LOG.info("Checking AWS connection");
        } catch (AmazonSQSException e) {
            LOG.error("Problem with connection. Check AWS credentials. {}", e.getErrorMessage(), e);
            counters.getCloudConnectionStatus().set(0);
        } catch (Exception e) {
            LOG.error("Problem with connection. {}", e.getMessage(), e);
            counters.getCloudConnectionStatus().set(0);
        }
    }
}
