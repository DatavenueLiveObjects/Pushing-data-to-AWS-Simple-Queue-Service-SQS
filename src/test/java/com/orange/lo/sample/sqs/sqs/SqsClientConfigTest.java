package com.orange.lo.sample.sqs.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SqsClientConfigTest {

    private SqsClientConfig sqsClientConfig;

    @BeforeEach
    void setUp() {
        SqsProperties sqsProperties = new SqsProperties();
        sqsProperties.setThreadPoolSize(20);
        sqsProperties.setTaskQueueSize(20);
        sqsClientConfig = new SqsClientConfig(sqsProperties);
    }

    @Test
    void shouldCreateAmazonSQSBean() {
        AmazonSQS amazonSQS = sqsClientConfig.amazonSQS();

        assertNotNull(amazonSQS);
    }

    @Test
    void shouldCreateThreadPoolExecutorBean() {
        ThreadPoolExecutor sqsSender = sqsClientConfig.threadPoolExecutor();

        assertNotNull(sqsSender);
    }
}