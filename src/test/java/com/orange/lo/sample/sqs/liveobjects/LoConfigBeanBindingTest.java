/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.liveobjects;

import com.orange.lo.sample.sqs.config.LOApiClientConfiguration;
import com.orange.lo.sdk.LOApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import({LOApiClientConfiguration.class})
class LoConfigBeanBindingTest {

    @Autowired
    public LOApiClient loApiClient;

    @Test
    public void shouldAllBeansProperlyBinded() {
        assertNotNull(loApiClient);
    }
}