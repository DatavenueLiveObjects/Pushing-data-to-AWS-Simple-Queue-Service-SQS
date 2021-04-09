package com.orange.lo.sample.sqs.sqs;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sqs.AmazonSQS;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeException;
import net.jodah.failsafe.RetryPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
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
        ThreadPoolExecutor threadPoolExecutor = sqsClientConfig.threadPoolExecutor();

        assertNotNull(threadPoolExecutor);
    }

    @Test
    void shouldNotHandleExceptionBySendMessageRetryPolicyIfNotRetryableAmazonClientException() {
        RetryPolicy<Void> sendMessageRetryPolicy = sqsClientConfig.sendMessageRetryPolicy();

        assertThrows(RuntimeException.class, () -> Failsafe.with(sendMessageRetryPolicy).run((executionContext) -> {
            throw new RuntimeException();
        }));
    }

    @Test
    void shouldHandleExceptionBySendMessageRetryPolicyIfRetryableAmazonClientException() {
        RetryPolicy<Void> sendMessageRetryPolicy = sqsClientConfig.sendMessageRetryPolicy();

        AtomicInteger attemptCount = new AtomicInteger(-1);

        Failsafe.with(sendMessageRetryPolicy).run((executionContext) -> {
            attemptCount.set(executionContext.getAttemptCount());
            if (executionContext.getAttemptCount() == 0) {
                throw new RetryableAmazonClientException(new AmazonClientException("message"), true);
            }
        });

        assertEquals(1, attemptCount.get());
    }

    @Test
    void shouldThrowExceptionBySendMessageRetryPolicyIfNotAllowToRetry() {
        RetryPolicy<Void> sendMessageRetryPolicy = sqsClientConfig.sendMessageRetryPolicy();

        AtomicInteger attemptCount = new AtomicInteger(-1);

        assertThrows(FailsafeException.class, () -> Failsafe.with(sendMessageRetryPolicy).run((executionContext) -> {
            attemptCount.set(executionContext.getAttemptCount());
            if (executionContext.getAttemptCount() == 0) {
                throw new RetryableAmazonClientException(new AmazonClientException("message"), false);
            }
        }));

        assertEquals(0, attemptCount.get());
    }
}