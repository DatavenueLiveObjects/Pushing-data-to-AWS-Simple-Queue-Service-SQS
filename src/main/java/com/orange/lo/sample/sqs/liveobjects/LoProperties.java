/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.liveobjects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lo")
public class LoProperties {

    private String hostname;
    private String apiKey;
    private String topic;
    private Integer synchronizationInterval;
    private Integer messageQos;
    private String mqttPersistenceDir;
    private Integer keepAliveIntervalSeconds;
    private Integer connectionTimeout;
    private Integer messageBatchSize;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getSynchronizationInterval() {
        return synchronizationInterval;
    }

    public void setSynchronizationInterval(Integer synchronizationInterval) {
        this.synchronizationInterval = synchronizationInterval;
    }

    public Integer getMessageQos() {
        return messageQos;
    }

    public void setMessageQos(Integer messageQos) {
        this.messageQos = messageQos;
    }

    public String getMqttPersistenceDir() {
        return mqttPersistenceDir;
    }

    public void setMqttPersistenceDir(String mqttPersistenceDir) {
        this.mqttPersistenceDir = mqttPersistenceDir;
    }

    public Integer getKeepAliveIntervalSeconds() {
        return keepAliveIntervalSeconds;
    }

    public void setKeepAliveIntervalSeconds(Integer keepAliveIntervalSeconds) {
        this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public Integer getMessageBatchSize() {
        return messageBatchSize;
    }

    public void setMessageBatchSize(Integer messageBatchSize) {
        this.messageBatchSize = messageBatchSize;
    }
}