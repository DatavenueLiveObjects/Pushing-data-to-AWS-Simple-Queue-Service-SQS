/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.sqs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "aws.sqs")
@Component
public class SqsProperties {

	private static final String SERVICE_PROFILE_NAME = "service-profile";
	private static final String CUSTOMER_PROFILE_NAME = "customer-profile";

    private String queueUrl;
    private Integer threadPoolSize;
    private Long connectionTimeout;
    private Integer taskQueueSize;
    private Long throttlingDelay;
    private Integer maxSendAttempts;
    private String messageGroupId;
    private String region;
    private Long keepAliveTime;
    
	public String getQueueUrl() {
		return queueUrl;
	}
	public void setQueueUrl(String queueUrl) {
		this.queueUrl = queueUrl;
	}
	public Integer getThreadPoolSize() {
		return threadPoolSize;
	}
	public void setThreadPoolSize(Integer threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}
	public Long getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(Long connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public Integer getTaskQueueSize() {
		return taskQueueSize;
	}
	public void setTaskQueueSize(Integer taskQueueSize) {
		this.taskQueueSize = taskQueueSize;
	}
	public Long getThrottlingDelay() {
		return throttlingDelay;
	}
	public void setThrottlingDelay(Long throttlingDelay) {
		this.throttlingDelay = throttlingDelay;
	}
	public Integer getMaxSendAttempts() {
		return maxSendAttempts;
	}
	public void setMaxSendAttempts(Integer maxSendAttempts) {
		this.maxSendAttempts = maxSendAttempts;
	}
	public String getMessageGroupId() {
		return messageGroupId;
	}
	public void setMessageGroupId(String messageGroupId) {
		this.messageGroupId = messageGroupId;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public Long getKeepAliveTime() {
		return keepAliveTime;
	}
	public void setKeepAliveTime(Long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}
	public String getServiceProfileName() {
		return SERVICE_PROFILE_NAME;
	}
	public String getCustomerProfileName() {
		return CUSTOMER_PROFILE_NAME;
	}
}