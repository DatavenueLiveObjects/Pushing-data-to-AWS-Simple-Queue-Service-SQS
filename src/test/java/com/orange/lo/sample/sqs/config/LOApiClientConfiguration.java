/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifo;

@TestConfiguration
public class LOApiClientConfiguration {

    static {
        System.setProperty("aws.region", "eu-west-1");
    }

    @Bean
    public LOApiClient loApiClient() {
        DataManagementFifo dataManagementFifo = Mockito.mock(DataManagementFifo.class);
        LOApiClient loApiClient = Mockito.mock(LOApiClient.class);
        Mockito.when(loApiClient.getDataManagementFifo()).thenReturn(dataManagementFifo);
        return loApiClient;
    }
}