/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.liveobjects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lo")
public class LoProperties {

    private static final String DEFAULT_HOSTNAME = "liveobjects.orange-business.com";
    private static final int DEFAULT_MESSAGE_QOS = 1;
    private static final String DEFAULT_MQTT_PERSISTENCE_DIR = ".";

    private String hostname = DEFAULT_HOSTNAME;
    private String apiKey;
    private String topic;
    private int messageQos = DEFAULT_MESSAGE_QOS;
    private String mqttPersistenceDir = DEFAULT_MQTT_PERSISTENCE_DIR;
    private int keepAliveIntervalSeconds;
    private int connectionTimeout;
    private int synchronizationInterval;

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

    public int getMessageQos() {
        return messageQos;
    }

    public void setMessageQos(int messageQos) {
        this.messageQos = messageQos;
    }

    public String getMqttPersistenceDir() {
        return mqttPersistenceDir;
    }

    public void setMqttPersistenceDir(String mqttPersistenceDir) {
        this.mqttPersistenceDir = mqttPersistenceDir;
    }

    public int getKeepAliveIntervalSeconds() {
        return keepAliveIntervalSeconds;
    }

    public void setKeepAliveIntervalSeconds(int keepAliveIntervalSeconds) {
        this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getSynchronizationInterval() {
        return synchronizationInterval;
    }

    public void setSynchronizationInterval(int synchronizationInterval) {
        this.synchronizationInterval = synchronizationInterval;
    }
}