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
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.orange.lo.sample.sqs.utils.Counters;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    private final AmazonSQS sqs;
    private final Counters counters;
    private final ThreadPoolExecutor tpe;
    private final Random random;
    private final RetryPolicy<Void> sendMessageRetryPolicy;
    private final RetryPolicy<Void> executeTaskRetryPolicy;
    private final RetryCondition amazonRetryCondition;

    public SqsSender(
            AmazonSQS amazonSQ,
            SqsProperties sqsProperties,
            ThreadPoolExecutor tpe,
            Counters counters,
            RetryPolicy<Void> sendMessageRetryPolicy,
            RetryPolicy<Void> executeTaskRetryPolicy,
            RetryCondition amazonRetryCondition
    ) {
        this.sqs = amazonSQ;
        this.sqsProperties = sqsProperties;
        this.random = new Random();
        this.tpe = tpe;
        this.counters = counters;
        this.sendMessageRetryPolicy = sendMessageRetryPolicy;
        this.executeTaskRetryPolicy = executeTaskRetryPolicy;
        this.amazonRetryCondition = amazonRetryCondition;
    }

    public void send(List<String> messages) {
        Failsafe.with(executeTaskRetryPolicy)
            .run(() -> {
              tpe.submit(() -> {
                Failsafe.with(sendMessageRetryPolicy)
                    .onFailure(test -> counters.getMesasageSentAttemptFailedCounter().increment(messages.size()))
                    .run(executionContext -> {
                      counters.getMesasageSentAttemptCounter().increment(messages.size());
                      sendBatches(messages, executionContext.getAttemptCount());
                    });
            });
        });
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
            counters.getMesasageSentCounter().increment(sendBatchRequest.getEntries().size());
        } catch (final AmazonClientException ace) {
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
}
