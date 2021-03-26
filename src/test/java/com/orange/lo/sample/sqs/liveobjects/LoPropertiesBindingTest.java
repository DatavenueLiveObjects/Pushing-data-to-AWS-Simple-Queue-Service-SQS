package com.orange.lo.sample.sqs.liveobjects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(LoProperties.class)
@TestPropertySource(value = "classpath:config/application.yml")
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
class LoPropertiesBindingTest {

    @Autowired
    private LoProperties loProperties;

    @Test
    public void shouldBindLoPropertiesFromYamlConfiguration() {
        Assertions.assertEquals("test-hostname", loProperties.getHostname());
        Assertions.assertEquals("test-api-key", loProperties.getApiKey());
        Assertions.assertEquals("test-topic", loProperties.getTopic());
        Assertions.assertEquals(123, (int) loProperties.getSynchronizationInterval());
        Assertions.assertEquals(234, (int) loProperties.getMessageQos());
        Assertions.assertEquals("/tmp/test", loProperties.getMqttPersistenceDir());
        Assertions.assertEquals(345, (int) loProperties.getKeepAliveIntervalSeconds());
        Assertions.assertEquals(456, (int) loProperties.getConnectionTimeout());
        Assertions.assertEquals(567, (int) loProperties.getMessageBatchSize());
    }

}
