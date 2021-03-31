/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

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
