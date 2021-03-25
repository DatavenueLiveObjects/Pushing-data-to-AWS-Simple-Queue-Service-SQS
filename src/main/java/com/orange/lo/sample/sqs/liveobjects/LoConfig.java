/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.liveobjects;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.LOApiClientParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;
import java.util.Collections;

@Configuration
public class LoConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LoProperties loProperties;

    private final LoMqttHandler loMqttHandler;

    public LoConfig(LoProperties loProperties, LoMqttHandler loMqttHandler) {
        this.loProperties = loProperties;
        this.loMqttHandler = loMqttHandler;
    }

    @Bean
    public LOApiClient loApiClient() {
        LOGGER.debug("LoConfig init...");
        LOApiClientParameters parameters = loApiClientParameters();
        return new LOApiClient(parameters);
    }

    private LOApiClientParameters loApiClientParameters() {
        return LOApiClientParameters.builder()
                .hostname(loProperties.getHostname())
                .apiKey(loProperties.getApiKey())
                .mqttPersistenceDataDir(loProperties.getMqttPersistenceDir())
                .dataManagementMqttCallback(loMqttHandler)
                .topics(Collections.singletonList(loProperties.getTopic()))
                .keepAliveIntervalSeconds(loProperties.getKeepAliveIntervalSeconds())
                .connectionTimeout(loProperties.getConnectionTimeout())
                .messageQos(loProperties.getMessageQos())
                .automaticReconnect(true)
                .build();
    }
}