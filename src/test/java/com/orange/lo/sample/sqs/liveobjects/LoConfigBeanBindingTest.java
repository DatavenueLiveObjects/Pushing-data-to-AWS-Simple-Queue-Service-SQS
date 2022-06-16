/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.liveobjects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.orange.lo.sample.sqs.config.LOApiClientConfiguration;
import com.orange.lo.sdk.LOApiClient;

@SpringBootTest
@ContextConfiguration(classes = { LOApiClientConfiguration.class })
class LoConfigBeanBindingTest {

    @Autowired
    public LOApiClient loApiClient;

    @Test
    public void shouldAllBeansProperlyBinded() {
        assertNotNull(loApiClient);
    }
}