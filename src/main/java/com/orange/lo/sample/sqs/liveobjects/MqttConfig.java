/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.sqs.liveobjects;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;

import java.lang.invoke.MethodHandles;

@EnableIntegration
@Configuration
public class MqttConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private MqttProperties lomProperties;
    private MqttHandler mqttHandler;

    public MqttConfig(MqttProperties lomProperties, MqttHandler mqttHandler) {
        LOG.info("MqttConfig init...");
        this.lomProperties = lomProperties;
        this.mqttHandler = mqttHandler;
    }

    public MqttPahoClientFactory mqttClientFactory() {
        LOG.info("Connecting to mqtt server: {}", lomProperties.getUri());
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setServerURIs(new String[]{lomProperties.getUri()});
        opts.setUserName(lomProperties.getUsername());
        opts.setPassword(lomProperties.getApiKey().toCharArray());
        opts.setKeepAliveInterval(lomProperties.getKeepAliveIntervalSeconds());
        opts.setConnectionTimeout(lomProperties.getConnectionTimeout());
        factory.setConnectionOptions(opts);
        return factory;
    }

    @Bean
    public MessageProducerSupport mqttInbound() {
        LOG.info("Connecting to mqtt topics: {}", lomProperties.getTopic());
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("mqtt2sqs",
                mqttClientFactory(),
                lomProperties.getTopic()
        );

        adapter.setAutoStartup(false);
        adapter.setRecoveryInterval(lomProperties.getRecoveryInterval());
        adapter.setCompletionTimeout(lomProperties.getCompletionTimeout());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(lomProperties.getQos());
        return adapter;
    }

    @Bean
    public IntegrationFlow mqttInFlow() {
        return IntegrationFlows.from(mqttInbound())
                .handle(message -> mqttHandler.handleMessage((Message<String>) message))
                .get();
    }

}