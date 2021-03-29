package com.orange.lo.sample.sqs.sqs;

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
@EnableConfigurationProperties(SqsProperties.class)
@TestPropertySource(value = "classpath:config/application.yml")
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
class SqsPropertiesBindingTest {

    @Autowired
    private SqsProperties sqsProperties;

    @Test
    public void shouldBindLoPropertiesFromYamlConfiguration() {
        Assertions.assertEquals("test-queue-url", sqsProperties.getQueueUrl());
        Assertions.assertEquals(123, sqsProperties.getThreadPoolSize());
        Assertions.assertEquals(234, sqsProperties.getConnectionTimeout());
        Assertions.assertEquals(345, sqsProperties.getTaskQueueSize());
        Assertions.assertEquals(456, sqsProperties.getThrottlingDelay());
        Assertions.assertEquals(567, sqsProperties.getMaxSendAttempts());
        Assertions.assertEquals("test-message-group", sqsProperties.getMessageGroupId());
        Assertions.assertEquals("test-region", sqsProperties.getRegion());
    }

}