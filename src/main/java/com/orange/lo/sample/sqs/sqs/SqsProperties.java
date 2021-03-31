/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.sqs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "aws.sqs")
public class SqsProperties {

    private String queueUrl;
    private Integer threadPoolSize;
    private Long connectionTimeout;
    private Integer taskQueueSize;
    private Long throttlingDelay;
    private Integer maxSendAttempts;
    private String messageGroupId;
    private String region;

    public SqsProperties(
            String queueUrl,
            Integer threadPoolSize,
            Long connectionTimeout,
            Integer taskQueueSize,
            Long throttlingDelay,
            Integer maxSendAttempts,
            String messageGroupId,
            String region
    ) {
        this.queueUrl = queueUrl;
        this.threadPoolSize = threadPoolSize;
        this.connectionTimeout = connectionTimeout;
        this.taskQueueSize = taskQueueSize;
        this.throttlingDelay = throttlingDelay;
        this.maxSendAttempts = maxSendAttempts;
        this.messageGroupId = messageGroupId;
        this.region = region;
    }

    public String getQueueUrl() {
        return queueUrl;
    }

    public Integer getThreadPoolSize() {
        return threadPoolSize;
    }

    public Long getConnectionTimeout() {
        return connectionTimeout;
    }

    public Integer getTaskQueueSize() {
        return taskQueueSize;
    }

    public Long getThrottlingDelay() {
        return throttlingDelay;
    }

    public Integer getMaxSendAttempts() {
        return maxSendAttempts;
    }

    public String getMessageGroupId() {
        return messageGroupId;
    }

    public String getRegion() {
        return region;
    }
}