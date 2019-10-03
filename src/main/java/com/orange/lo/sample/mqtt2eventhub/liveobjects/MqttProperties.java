/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lo-mqtt")
public class MqttProperties {

    private String uri;
    private String username;
    private String apiKey;
    private String topic;
    private int recoveryInterval;
    private int completionTimeout;
    private int connectionTimeout;
    private int qos;
    private int keepAliveIntervalSeconds;

    public void setUri(String uri) { this.uri = uri; }
    public String getUri() { return uri; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public int getRecoveryInterval() { return recoveryInterval; }
    public void setRecoveryInterval(int recoveryInterval) { this.recoveryInterval = recoveryInterval; }

    public int getCompletionTimeout() { return completionTimeout; }
    public void setCompletionTimeout(int completionTimeout) { this.completionTimeout = completionTimeout; }

    public int getConnectionTimeout() { return connectionTimeout; }
    public void setConnectionTimeout(int connectionTimeout) { this.connectionTimeout = connectionTimeout; }

    public int getQos() { return qos; }
    public void setQos(int qos) { this.qos = qos; }

    public int getKeepAliveIntervalSeconds() { return keepAliveIntervalSeconds; }
    public void setKeepAliveIntervalSeconds(int keepAliveIntervalSeconds) { this.keepAliveIntervalSeconds = keepAliveIntervalSeconds; }

    @Override
    public String toString() {
        return "MqttProperties{" +
                "uri='" + uri + '\'' +
                ", username='" + username + '\'' +
                ", topic='" + topic + '\'' +
                ", recoveryInterval=" + recoveryInterval +
                ", completionTimeout=" + completionTimeout +
                ", connectionTimeout=" + connectionTimeout +
                ", qos=" + qos +
                ", keepAliveIntervalSeconds=" + keepAliveIntervalSeconds +
                '}';
    }
}
