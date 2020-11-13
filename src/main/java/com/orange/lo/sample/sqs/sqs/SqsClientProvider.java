/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.sqs;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;

import com.orange.lo.sample.sqs.Counters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.Random;

@Configuration
public class SqsClientProvider {

    private Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private Counters counters;
    private AmazonSQS sqs;
    private final String myQueueUrl;
    private ThreadPoolExecutor tpe;
    private Random random;

    @Autowired
    public SqsClientProvider(SqsProperties sqsProperties, Counters counters) {
        log.info("SqsClientProvider init...");
        sqs = AmazonSQSClientBuilder.standard().withRegion("eu-central-1").build();
        myQueueUrl = sqsProperties.getQueueUrl();
        this.counters = counters;
        BlockingQueue<Runnable> tasks = new ArrayBlockingQueue<>(sqsProperties.getTaskQueueSize());
        tpe = new ThreadPoolExecutor(sqsProperties.getThreadPoolSize(), sqsProperties.getThreadPoolSize(), 10, TimeUnit.SECONDS, tasks);
        random = new Random();
    }

    @Bean
    public SqsSender sqsSender() {
        return message -> {
            try {
                tpe.submit(() -> {
                    counters.evtAttemptCount().increment();
                    send(message, 0);
                });
            } catch (RejectedExecutionException rejected) {
                counters.evtRejected().increment();
                log.error("Too many tasks in queue, rejecting: {}", message.hashCode());
            }
        };
    }

    //send batches
    private void send(List<String> batch, int attemptCount) {
        if (attemptCount > 0)
            log.info("Retrying to send ({}): {}", attemptCount, batch.hashCode());
        List<SendMessageBatchRequestEntry> entries = new ArrayList<>(10);
        int i = 0;
        try {//send a batch
            for (String message : batch) {
                double deduplicationId = random.nextDouble();
                entries.add(new SendMessageBatchRequestEntry(String.valueOf(i++), message).withMessageGroupId("messagegroup1").withMessageDeduplicationId(Double.toString(deduplicationId)));
            }
            SendMessageBatchRequest sendBatchRequest = new SendMessageBatchRequest()
                    .withQueueUrl(myQueueUrl)
                    .withEntries(entries);
            sqs.sendMessageBatch(sendBatchRequest);
            counters.evtSuccess().increment(sendBatchRequest.getEntries().size());
        } catch (final AmazonServiceException ase) {
            log.error("Caught an AmazonServiceException, which means " +
                    "your request made it to Amazon SQS, but was " +
                    "rejected with an error response for some reason.");
            log.error("Error Message:    {}", ase.getMessage());
            log.error("HTTP Status Code: {}", ase.getStatusCode());
            log.error("AWS Error Code:   {}", ase.getErrorCode());
            log.error("Error Type:       {}", ase.getErrorType());
            log.error("Request ID:       {}", ase.getRequestId());
        } catch (final AmazonClientException ace) {
            log.error("Caught an AmazonClientException, which means " +
                    "the client encountered a serious internal problem while " +
                    "trying to communicate with Amazon SQS, such as not " +
                    "being able to access the network.");
            log.error("Error Message: {}", ace.getMessage());
        }
    }

    @Scheduled(fixedRate = 30000)
    public void reportExecutorData() {
        log.info("Pool size: {}, active threads: {}, tasks in queue: {}", tpe.getPoolSize(), tpe.getActiveCount(), tpe.getQueue().size());
    }

}