/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.modify;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

import org.ini4j.Ini;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.orange.lo.sample.sqs.ConnectorApplication;
import com.orange.lo.sample.sqs.liveobjects.LoProperties;
import com.orange.lo.sample.sqs.sqs.SqsProperties;

@Component
public class ModifyConfigurationService {

	private LoProperties loProperties;
	private SqsProperties sqsProperties;
	private ProfileCredentialsProvider profileCredentialsProvider;
	private File configurationFile;
	private File awsConfigurationFile;

	public ModifyConfigurationService(LoProperties loProperties, SqsProperties sqsProperties,
			ProfileCredentialsProvider profileCredentialsProvider, File configurationFile, File awsConfigurationFile) {
		this.loProperties = loProperties;
		this.sqsProperties = sqsProperties;
		this.profileCredentialsProvider = profileCredentialsProvider;
		this.configurationFile = configurationFile;
		this.awsConfigurationFile = awsConfigurationFile;
	}

	public ModifyConfigurationProperties getProperties() {
		AWSCredentials credentials = profileCredentialsProvider.getCredentials();

		ModifyConfigurationProperties modifyConfigurationProperties = new ModifyConfigurationProperties();
		modifyConfigurationProperties.setLoApiKey(loProperties.getApiKey());
		modifyConfigurationProperties.setLoConnectionTimeout(loProperties.getConnectionTimeout());
		modifyConfigurationProperties.setLoKeepAliveIntervalSeconds(loProperties.getKeepAliveIntervalSeconds());
		modifyConfigurationProperties.setLoMessageBatchSize(loProperties.getMessageBatchSize());
		modifyConfigurationProperties.setLoMessageQos(loProperties.getMessageQos());
		modifyConfigurationProperties.setLoSynchronizationInterval(loProperties.getSynchronizationInterval());
		modifyConfigurationProperties.setLoTopic(loProperties.getTopic());

		modifyConfigurationProperties.setSqsConnectionTimeout(sqsProperties.getConnectionTimeout());
		modifyConfigurationProperties.setSqsKeepAliveTime(sqsProperties.getKeepAliveTime());
		modifyConfigurationProperties.setSqsMaxSendAttempts(sqsProperties.getMaxSendAttempts());
		modifyConfigurationProperties.setSqsMessageGroupId(sqsProperties.getMessageGroupId());
		modifyConfigurationProperties.setSqsQueueUrl(sqsProperties.getQueueUrl());
		modifyConfigurationProperties.setSqsRegion(sqsProperties.getRegion());
		modifyConfigurationProperties.setSqsTaskQueueSize(sqsProperties.getTaskQueueSize());
		modifyConfigurationProperties.setSqsThreadPoolSize(sqsProperties.getThreadPoolSize());
		modifyConfigurationProperties.setSqsThrottlingDelay(sqsProperties.getThrottlingDelay());

		modifyConfigurationProperties.setAwsAccessKey(credentials.getAWSAccessKeyId());
		modifyConfigurationProperties.setAwsSecretAccessKey(credentials.getAWSSecretKey());

		return modifyConfigurationProperties;
	}

	public void modify(ModifyConfigurationProperties modifyConfigurationProperties) {

		try {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			ObjectNode root = (ObjectNode) mapper.readTree(configurationFile);

			ObjectNode loNode = (ObjectNode) root.get("lo");

			setProperty(loNode, "api-key", () -> modifyConfigurationProperties.getLoApiKey());
			setProperty(loNode, "topic", () -> modifyConfigurationProperties.getLoTopic());
			setProperty(loNode, "synchronization-interval",
					() -> modifyConfigurationProperties.getLoSynchronizationInterval());
			setProperty(loNode, "message-qos", () -> modifyConfigurationProperties.getLoMessageQos());
			setProperty(loNode, "keep-alive-interval-seconds",
					() -> modifyConfigurationProperties.getLoKeepAliveIntervalSeconds());
			setProperty(loNode, "connection-timeout", () -> modifyConfigurationProperties.getLoConnectionTimeout());
			setProperty(loNode, "message-batch-size", () -> modifyConfigurationProperties.getLoMessageBatchSize());

			ObjectNode sqsNode = (ObjectNode) root.get("aws").get("sqs");

			setProperty(sqsNode, "queue-url", () -> modifyConfigurationProperties.getSqsQueueUrl());
			setProperty(sqsNode, "thread-pool-size", () -> modifyConfigurationProperties.getSqsThreadPoolSize());
			setProperty(sqsNode, "connection-timeout", () -> modifyConfigurationProperties.getSqsConnectionTimeout());
			setProperty(sqsNode, "task-queue-size", () -> modifyConfigurationProperties.getSqsTaskQueueSize());
			setProperty(sqsNode, "throttling-delay", () -> modifyConfigurationProperties.getSqsThrottlingDelay());
			setProperty(sqsNode, "max-send-attempts", () -> modifyConfigurationProperties.getSqsMaxSendAttempts());
			setProperty(sqsNode, "message-group-id", () -> modifyConfigurationProperties.getSqsMessageGroupId());
			setProperty(sqsNode, "region", () -> modifyConfigurationProperties.getSqsRegion());
			setProperty(sqsNode, "keep-alive-time", () -> modifyConfigurationProperties.getSqsKeepAliveTime());

			mapper.writer().writeValue(configurationFile, root);

			Ini awsProps = new Ini(awsConfigurationFile);
			setProperty(awsProps, "aws_access_key_id", () -> modifyConfigurationProperties.getAwsAccessKey());
			setProperty(awsProps, "aws_secret_access_key", () -> modifyConfigurationProperties.getAwsSecretAccessKey());
			awsProps.store();

			ConnectorApplication.restart();
		} catch (IOException e) {
			throw new ModifyException("Error while modifying configuration", e);
		}
	}

	private void setProperty(Ini awsProps, String parameterName, Supplier<String> parameterSupplier) {
		String parameter = parameterSupplier.get();

		if (Objects.isNull(parameter) || parameter.isEmpty()) {
			return;
		}
		awsProps.put("default", parameterName, parameter);
	}

	private void setProperty(ObjectNode node, String parameterName, Supplier<Object> parameterSupplier) {

		Object parameter = parameterSupplier.get();
		if (Objects.isNull(parameter)) {
			return;
		}

		if (parameter instanceof Integer) {
			node.put(parameterName, (Integer) parameter);
		} else if (parameter instanceof Long) {
			node.put(parameterName, (Long) parameter);
		} else {
			node.put(parameterName, String.valueOf(parameter));
		}
	}
}