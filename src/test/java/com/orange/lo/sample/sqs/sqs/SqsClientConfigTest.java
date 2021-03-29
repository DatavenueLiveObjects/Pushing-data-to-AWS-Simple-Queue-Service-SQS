package com.orange.lo.sample.sqs.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SqsClientConfigTest {

    private SqsClientConfig sqsClientConfig;

    @Mock
    private SqsProperties sqsProperties;

    @BeforeEach
    void setUp() {
        sqsClientConfig = new SqsClientConfig(sqsProperties);
    }

    @Test
    void shouldCreateAmazonSQSBean() {
        when(sqsProperties.getRegion()).thenReturn("test-region");
        AmazonSQS amazonSQS = sqsClientConfig.amazonSQS();

        assertNotNull(amazonSQS);
    }

    @Test
    void shouldCreateThreadPoolExecutorBean() {
        when(sqsProperties.getThreadPoolSize()).thenReturn(20);
        when(sqsProperties.getTaskQueueSize()).thenReturn(20);
        ThreadPoolExecutor sqsSender = sqsClientConfig.threadPoolExecutor();

        assertNotNull(sqsSender);
    }
}