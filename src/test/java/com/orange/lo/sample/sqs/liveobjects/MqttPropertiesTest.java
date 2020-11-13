package com.orange.lo.sample.sqs.liveobjects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MqttPropertiesTest {

    private MqttProperties mqttProperties;

    @BeforeEach
    void setUp() {
        mqttProperties = new MqttProperties();
    }

    @Test
    void shouldSetUri() {
        String uri = "ssl://liveobjects.orange-business.com:8883";
        mqttProperties.setUri(uri);

        assertEquals(uri, mqttProperties.getUri());
    }

    @Test
    void shouldSetApiKey() {
        String apiKey = "aBcDEF132";
        mqttProperties.setApiKey(apiKey);

        assertEquals(apiKey, mqttProperties.getApiKey());
    }

    @Test
    void shouldSetTopic() {
        String topic = "fifo/topic";
        mqttProperties.setTopic(topic);

        assertEquals(topic, mqttProperties.getTopic());
    }

    @Test
    void shouldSetRecoveryInterval() {
        int recoveryInterval = 1000;
        mqttProperties.setRecoveryInterval(recoveryInterval);

        assertEquals(recoveryInterval, mqttProperties.getRecoveryInterval());
    }

    @Test
    void shouldSetCompletionTimeout() {
        int completionTimeout = 2000;
        mqttProperties.setCompletionTimeout(completionTimeout);

        assertEquals(completionTimeout, mqttProperties.getCompletionTimeout());
    }

    @Test
    void shouldSetConnectionTimeout() {
        int connectionTimeout = 3000;
        mqttProperties.setConnectionTimeout(connectionTimeout);

        assertEquals(connectionTimeout, mqttProperties.getConnectionTimeout());
    }

    @Test
    void shouldSetQos() {
        int qos = 0;
        mqttProperties.setQos(qos);

        assertEquals(qos, mqttProperties.getQos());
    }

    @Test
    void shouldSetKeepAliveIntervalSeconds() {
        int keepAliveIntervalSeconds = 4000;
        mqttProperties.setKeepAliveIntervalSeconds(keepAliveIntervalSeconds);

        assertEquals(keepAliveIntervalSeconds, mqttProperties.getKeepAliveIntervalSeconds());
    }
}