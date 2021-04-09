/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.liveobjects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "lo")
public class LoProperties {

    private final String hostname;
    private final String apiKey;
    private final String topic;
    private final Integer synchronizationInterval;
    private final Integer messageQos;
    private final String mqttPersistenceDir;
    private final Integer keepAliveIntervalSeconds;
    private final Integer connectionTimeout;
    private final Integer messageBatchSize;

    public LoProperties(
            String hostname,
            String apiKey,
            String topic,
            Integer synchronizationInterval,
            Integer messageQos,
            String mqttPersistenceDir,
            Integer keepAliveIntervalSeconds,
            Integer connectionTimeout,
            Integer messageBatchSize
    ) {
        this.hostname = hostname;
        this.apiKey = apiKey;
        this.topic = topic;
        this.synchronizationInterval = synchronizationInterval;
        this.messageQos = messageQos;
        this.mqttPersistenceDir = mqttPersistenceDir;
        this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
        this.connectionTimeout = connectionTimeout;
        this.messageBatchSize = messageBatchSize;
    }

    public String getHostname() {
        return hostname;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getTopic() {
        return topic;
    }

    public Integer getSynchronizationInterval() {
        return synchronizationInterval;
    }

    public Integer getMessageQos() {
        return messageQos;
    }

    public String getMqttPersistenceDir() {
        return mqttPersistenceDir;
    }

    public Integer getKeepAliveIntervalSeconds() {
        return keepAliveIntervalSeconds;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public Integer getMessageBatchSize() {
        return messageBatchSize;
    }
}