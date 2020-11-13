package com.orange.lo.sample.sqs.sqs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqsPropertiesTest {

    private SqsProperties sqsProperties;

    @BeforeEach
    void setUp() {
        sqsProperties = new SqsProperties();
    }

    @Test
    void shouldSetQueueUrl() {
        String queueUrl = "queue-url";
        sqsProperties.setQueueUrl(queueUrl);

        assertEquals(queueUrl, sqsProperties.getQueueUrl());
    }

    @Test
    void shouldSetThreadPoolSize() {
        int threadPoolSize = 10;
        sqsProperties.setThreadPoolSize(threadPoolSize);

        assertEquals(threadPoolSize, sqsProperties.getThreadPoolSize());
    }

    @Test
    void shouldSetConnectionTimeout() {
        int connectionTimeout = 1000;
        sqsProperties.setConnectionTimeout(connectionTimeout);

        assertEquals(connectionTimeout, sqsProperties.getConnectionTimeout());
    }

    @Test
    void shouldSetTaskQueueSize() {
        int taskQueueSize = 30;
        sqsProperties.setTaskQueueSize(taskQueueSize);

        assertEquals(taskQueueSize, sqsProperties.getTaskQueueSize());
    }

    @Test
    void shouldSetThrottlingDelay() {
        int throttlingDelay = 500;
        sqsProperties.setThrottlingDelay(throttlingDelay);

        assertEquals(throttlingDelay, sqsProperties.getThrottlingDelay());
    }

    @Test
    void shouldSetMaxSendAttempts() {
        int maxSendAttempts = 5;
        sqsProperties.setMaxSendAttempts(maxSendAttempts);

        assertEquals(maxSendAttempts, sqsProperties.getMaxSendAttempts());
    }
}