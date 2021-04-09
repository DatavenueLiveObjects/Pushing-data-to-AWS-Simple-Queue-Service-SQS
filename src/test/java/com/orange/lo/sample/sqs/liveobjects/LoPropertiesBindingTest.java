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
@TestPropertySource(value = "classpath:application.yml")
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
class LoPropertiesBindingTest {

    @Autowired
    private LoProperties loProperties;

    @Test
    public void shouldBindLoPropertiesFromYamlConfiguration() {
        Assertions.assertEquals("test-hostname", loProperties.getHostname());
        Assertions.assertEquals("test-api-key", loProperties.getApiKey());
        Assertions.assertEquals("test-topic", loProperties.getTopic());
        Assertions.assertEquals(123, loProperties.getSynchronizationInterval());
        Assertions.assertEquals(234, loProperties.getMessageQos());
        Assertions.assertEquals("/tmp/test", loProperties.getMqttPersistenceDir());
        Assertions.assertEquals(345, loProperties.getKeepAliveIntervalSeconds());
        Assertions.assertEquals(456, loProperties.getConnectionTimeout());
        Assertions.assertEquals(567, loProperties.getMessageBatchSize());
    }

}
