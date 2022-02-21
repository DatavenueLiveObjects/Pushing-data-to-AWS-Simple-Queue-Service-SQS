/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.orange.lo.sample.sqs.config.LOApiClientConfiguration;
import net.jodah.failsafe.RetryPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import({LOApiClientConfiguration.class})
class SqsClientConfigBeanBindingTest {

    @Autowired
    public AmazonSQS amazonSQS;

    @Autowired
    public ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    public com.amazonaws.retry.RetryPolicy.RetryCondition amazonRetryCondition;

    @Autowired
    public RetryPolicy<Void> executeTaskRetryPolicy;

    @Autowired
    public RetryPolicy<Void> sendMessageRetryPolicy;

    @Test
    public void shouldAllBeansProperlyBinded() {
        assertNotNull(amazonSQS);
        assertNotNull(threadPoolExecutor);
        assertNotNull(amazonRetryCondition);
        assertNotNull(executeTaskRetryPolicy);
        assertNotNull(sendMessageRetryPolicy);
    }
}