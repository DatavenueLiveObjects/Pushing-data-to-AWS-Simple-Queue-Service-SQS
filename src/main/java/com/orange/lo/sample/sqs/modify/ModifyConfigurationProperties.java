/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.modify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModifyConfigurationProperties {

    private String loApiKey;
    private String loTopic;
    private Integer loSynchronizationInterval;
    private Integer loMessageQos;
    private Integer loKeepAliveIntervalSeconds;
    private Integer loConnectionTimeout;
    private Integer loMessageBatchSize;
    
    private String sqsQueueUrl;
    private Integer sqsThreadPoolSize;
    private Long sqsConnectionTimeout;
    private Integer sqsTaskQueueSize;
    private Long sqsThrottlingDelay;
    private Integer sqsMaxSendAttempts;
    private String sqsMessageGroupId;
    private String sqsRegion;
    private Long sqsKeepAliveTime;
    
    private String awsAccessKey;
	private String awsSecretAccessKey;
	
	public String getLoApiKey() {
		return loApiKey;
	}
	public void setLoApiKey(String loApiKey) {
		this.loApiKey = loApiKey;
	}
	public String getLoTopic() {
		return loTopic;
	}
	public void setLoTopic(String loTopic) {
		this.loTopic = loTopic;
	}
	public Integer getLoSynchronizationInterval() {
		return loSynchronizationInterval;
	}
	public void setLoSynchronizationInterval(Integer loSynchronizationInterval) {
		this.loSynchronizationInterval = loSynchronizationInterval;
	}
	public Integer getLoMessageQos() {
		return loMessageQos;
	}
	public void setLoMessageQos(Integer loMessageQos) {
		this.loMessageQos = loMessageQos;
	}
	public Integer getLoKeepAliveIntervalSeconds() {
		return loKeepAliveIntervalSeconds;
	}
	public void setLoKeepAliveIntervalSeconds(Integer loKeepAliveIntervalSeconds) {
		this.loKeepAliveIntervalSeconds = loKeepAliveIntervalSeconds;
	}
	public Integer getLoConnectionTimeout() {
		return loConnectionTimeout;
	}
	public void setLoConnectionTimeout(Integer loConnectionTimeout) {
		this.loConnectionTimeout = loConnectionTimeout;
	}
	public Integer getLoMessageBatchSize() {
		return loMessageBatchSize;
	}
	public void setLoMessageBatchSize(Integer loMessageBatchSize) {
		this.loMessageBatchSize = loMessageBatchSize;
	}
	public String getSqsQueueUrl() {
		return sqsQueueUrl;
	}
	public void setSqsQueueUrl(String sqsQueueUrl) {
		this.sqsQueueUrl = sqsQueueUrl;
	}
	public Integer getSqsThreadPoolSize() {
		return sqsThreadPoolSize;
	}
	public void setSqsThreadPoolSize(Integer sqsThreadPoolSize) {
		this.sqsThreadPoolSize = sqsThreadPoolSize;
	}
	public Long getSqsConnectionTimeout() {
		return sqsConnectionTimeout;
	}
	public void setSqsConnectionTimeout(Long sqsConnectionTimeout) {
		this.sqsConnectionTimeout = sqsConnectionTimeout;
	}
	public Integer getSqsTaskQueueSize() {
		return sqsTaskQueueSize;
	}
	public void setSqsTaskQueueSize(Integer sqsTaskQueueSize) {
		this.sqsTaskQueueSize = sqsTaskQueueSize;
	}
	public Long getSqsThrottlingDelay() {
		return sqsThrottlingDelay;
	}
	public void setSqsThrottlingDelay(Long sqsThrottlingDelay) {
		this.sqsThrottlingDelay = sqsThrottlingDelay;
	}
	public Integer getSqsMaxSendAttempts() {
		return sqsMaxSendAttempts;
	}
	public void setSqsMaxSendAttempts(Integer sqsMaxSendAttempts) {
		this.sqsMaxSendAttempts = sqsMaxSendAttempts;
	}
	public String getSqsMessageGroupId() {
		return sqsMessageGroupId;
	}
	public void setSqsMessageGroupId(String sqsMessageGroupId) {
		this.sqsMessageGroupId = sqsMessageGroupId;
	}
	public String getSqsRegion() {
		return sqsRegion;
	}
	public void setSqsRegion(String sqsRegion) {
		this.sqsRegion = sqsRegion;
	}
	public Long getSqsKeepAliveTime() {
		return sqsKeepAliveTime;
	}
	public void setSqsKeepAliveTime(Long sqsKeepAliveTime) {
		this.sqsKeepAliveTime = sqsKeepAliveTime;
	}
	
	public String getAwsAccessKey() {
		return awsAccessKey;
	}
	public void setAwsAccessKey(String awsAccessKey) {
		this.awsAccessKey = awsAccessKey;
	}
	public String getAwsSecretAccessKey() {
		return awsSecretAccessKey;
	}
	public void setAwsSecretAccessKey(String awsSecretAccessKey) {
		this.awsSecretAccessKey = awsSecretAccessKey;
	}
	@Override
	public String toString() {
		return "ModifyConfigurationProperties [loApiKey=***" + ", loTopic=" + loTopic
				+ ", loSynchronizationInterval=" + loSynchronizationInterval + ", loMessageQos=" + loMessageQos
				+ ", loKeepAliveIntervalSeconds=" + loKeepAliveIntervalSeconds + ", loConnectionTimeout="
				+ loConnectionTimeout + ", loMessageBatchSize=" + loMessageBatchSize + ", sqsQueueUrl=" + sqsQueueUrl
				+ ", sqsThreadPoolSize=" + sqsThreadPoolSize + ", sqsConnectionTimeout=" + sqsConnectionTimeout
				+ ", sqsTaskQueueSize=" + sqsTaskQueueSize + ", sqsThrottlingDelay=" + sqsThrottlingDelay
				+ ", sqsMaxSendAttempts=" + sqsMaxSendAttempts + ", sqsMessageGroupId=" + sqsMessageGroupId
				+ ", sqsRegion=" + sqsRegion + ", sqsKeepAliveTime=" + sqsKeepAliveTime + ", awsAccessKey=***"
				+ ", awsSecretAccessKey=***" + "]";
	}
}