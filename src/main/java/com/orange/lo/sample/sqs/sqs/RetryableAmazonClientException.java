package com.orange.lo.sample.sqs.sqs;

import com.amazonaws.AmazonClientException;

public class RetryableAmazonClientException extends Exception {

    AmazonClientException amazonClientException;
    boolean shouldRetry;

    public RetryableAmazonClientException(AmazonClientException amazonClientException, boolean shouldRetry) {
        this.amazonClientException = amazonClientException;
        this.shouldRetry = shouldRetry;
    }

    public AmazonClientException getAmazonClientException() {
        return amazonClientException;
    }

    public boolean isShouldRetry() {
        return shouldRetry;
    }

    @Override
    public String getMessage() {
        return amazonClientException.getMessage();
    }
}
