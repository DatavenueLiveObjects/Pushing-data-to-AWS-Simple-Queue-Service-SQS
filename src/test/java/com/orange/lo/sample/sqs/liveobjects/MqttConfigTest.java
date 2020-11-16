package com.orange.lo.sample.sqs.liveobjects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.endpoint.MessageProducerSupport;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class MqttConfigTest {

    @Mock
    private MqttHandler mqttHandler;
    private MqttConfig mqttConfig;

    @BeforeEach
    void setUp() {
        MqttProperties loPropertiesStub = new MqttProperties();
        loPropertiesStub.setUri("ssl://liveobjects.orange-business.com:8883");
        loPropertiesStub.setApiKey("abCDeF1");
        loPropertiesStub.setTopic("fifo/topic");
        mqttConfig = new MqttConfig(loPropertiesStub, mqttHandler);
    }

    @Test
    void shouldCreateMessageProducerSupport() {
        MessageProducerSupport messageProducerSupport = mqttConfig.mqttInbound();

        assertNotNull(messageProducerSupport);
    }

    @Test
    void shouldCreateIntegrationFlow() {
        IntegrationFlow integrationFlow = mqttConfig.mqttInFlow();

        assertNotNull(integrationFlow);
    }
}