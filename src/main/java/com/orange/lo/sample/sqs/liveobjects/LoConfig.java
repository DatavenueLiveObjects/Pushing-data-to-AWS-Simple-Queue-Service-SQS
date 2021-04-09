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
import org.springframework.util.StringUtils;

import java.lang.invoke.MethodHandles;
import java.util.Collections;

@Configuration
public class LoConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LoProperties loProperties;

    private final LoMqttHandler loMqttHandler;

    private final LOApiClientFactory loApiClientFactory;

    public LoConfig(LoProperties loProperties, LoMqttHandler loMqttHandler, LOApiClientFactory loApiClientFactory) {
        this.loProperties = loProperties;
        this.loMqttHandler = loMqttHandler;
        this.loApiClientFactory = loApiClientFactory;
    }

    @Bean
    public LOApiClient loApiClient() {
        LOGGER.debug("LoConfig init...");
        LOApiClientParameters parameters = getLoApiClientParameters();
        return loApiClientFactory.createLOApiClient(parameters);
    }

    private LOApiClientParameters getLoApiClientParameters() {
        if (StringUtils.isEmpty(loProperties.getTopic())) {
            throw new IllegalArgumentException("Topic is required");
        }

        LOApiClientParameters.LOApiClientParametersBuilder builder = LOApiClientParameters.builder()
                .apiKey(loProperties.getApiKey())
                .topics(Collections.singletonList(loProperties.getTopic()))
                .dataManagementMqttCallback(loMqttHandler)
                .automaticReconnect(true);

        if ( !StringUtils.isEmpty(loProperties.getHostname()) ) {
                builder.hostname(loProperties.getHostname());
        }

        if ( !StringUtils.isEmpty(loProperties.getMqttPersistenceDir()) ) {
            builder.mqttPersistenceDataDir(loProperties.getMqttPersistenceDir());
        }

        if ( loProperties.getMessageQos() != null ) {
            builder.messageQos(loProperties.getMessageQos());
        }

        if ( loProperties.getKeepAliveIntervalSeconds() != null ) {
            builder.keepAliveIntervalSeconds(loProperties.getKeepAliveIntervalSeconds());
        }

        if ( loProperties.getConnectionTimeout() != null ) {
            builder.connectionTimeout(loProperties.getConnectionTimeout());
        }

        return builder.build();
    }
}