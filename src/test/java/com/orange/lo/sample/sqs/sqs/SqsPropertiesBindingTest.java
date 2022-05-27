/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.sqs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(SqsProperties.class)
@TestPropertySource(value = "classpath:application.yml")
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
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
        Assertions.assertEquals(678, sqsProperties.getKeepAliveTime());
    }

}