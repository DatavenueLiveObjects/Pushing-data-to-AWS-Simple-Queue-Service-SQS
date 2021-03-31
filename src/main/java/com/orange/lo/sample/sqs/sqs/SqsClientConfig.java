/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.sqs;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.retry.PredefinedRetryPolicies;
import com.amazonaws.retry.RetryPolicy.RetryCondition;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

@Configuration
public class SqsClientConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SqsProperties sqsProperties;

    public SqsClientConfig(SqsProperties sqsProperties) {
        LOG.info("SqsClientConfig init...");
        this.sqsProperties = sqsProperties;
    }

    @Bean
    public AmazonSQS amazonSQS() {
        return AmazonSQSClientBuilder.standard().withRegion(sqsProperties.getRegion()).build();
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        BlockingQueue<Runnable> tasks = new ArrayBlockingQueue<>(sqsProperties.getTaskQueueSize());
        return new ThreadPoolExecutor(
                sqsProperties.getThreadPoolSize(),
                sqsProperties.getThreadPoolSize(),
                // TODO: keepAliveTime
                10,
                TimeUnit.SECONDS, tasks
        );
    }

    @Bean
    public RetryCondition amazonRetryCondition() {
        return new PredefinedRetryPolicies.SDKDefaultRetryCondition();
    }

    @Bean
    public RetryPolicy<Void> executeTaskRetryPolicy() {
        return new RetryPolicy<Void>().handleIf((e) -> e instanceof RejectedExecutionException);
    }

    @Bean
    public RetryPolicy<Void> sendMessageRetryPolicy() {
        return new RetryPolicy<Void>()
                .handleIf((e) -> {
                    if (isRetryableAmazonClientException(e)) {
                        return handleRetryableAmazonClientException((RetryableAmazonClientException) e);
                    }

                    return false;
                })
                .withMaxAttempts(-1)
                .withBackoff(1, 60, ChronoUnit.SECONDS)
                .withMaxDuration(Duration.ofHours(1));
    }

    private boolean handleRetryableAmazonClientException(RetryableAmazonClientException e) {
        if (e.getAmazonClientException() instanceof AmazonServiceException) {
            AmazonServiceException ace = (AmazonServiceException) e.getAmazonClientException();
            logAmazonServiceException(ace);
        } else {
            logAmazonClientException(e);
        }

        return e.isShouldRetry();
    }

    private void logAmazonClientException(RetryableAmazonClientException e) {
        StringBuilder sb = new StringBuilder("Caught an AmazonClientException, which means ")
                .append("the client encountered a serious internal problem while ")
                .append("trying to communicate with Amazon SQS, such as not ")
                .append("being able to access the network.\n")
                .append("Error Message: {}");
        LOG.error(sb.toString(), e.getMessage());
    }

    private void logAmazonServiceException(AmazonServiceException ace) {
        StringBuilder sb = new StringBuilder("Caught an AmazonServiceException, which means ")
                .append("your request made it to Amazon SQS, but was ")
                .append("rejected with an error response for some reason.\n")
                .append("Error Message:    {}\n")
                .append("HTTP Status Code: {}\n")
                .append("AWS Error Code:   {}\n")
                .append("Error Type:       {}\n")
                .append("Request ID:       {}");
        LOG.error(sb.toString(), ace.getMessage(), ace.getStatusCode(), ace.getErrorCode(), ace.getErrorType(),
                ace.getRequestId());
    }

    private boolean isRetryableAmazonClientException(Throwable e) {
        return e instanceof RetryableAmazonClientException;
    }
}