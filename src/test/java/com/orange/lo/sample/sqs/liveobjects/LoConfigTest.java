package com.orange.lo.sample.sqs.liveobjects;

import com.orange.lo.sdk.LOApiClientParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoConfigTest {

    public static final String API_KEY = "abcDEfgH123I";
    public static final String TOPIC = "topic";

    @Mock
    private LoMqttHandler loMqttHandler;

    @Mock
    private LOApiClientFactory loApiClientFactory;

    @Mock
    private LoProperties properties;

    @Captor
    private ArgumentCaptor<LOApiClientParameters> clientParametersCaptor;

    @Test
    void shouldCreateLoApiClientBasedOnLoPropertiesWithDefaultValues() {
        // given
        LoConfig loConfig = new LoConfig(getMinimumLoProperties(), loMqttHandler, loApiClientFactory);

        // when
        loConfig.loApiClient();

        // then
        verify(loApiClientFactory).createLOApiClient(clientParametersCaptor.capture());
        LOApiClientParameters clientParameters = clientParametersCaptor.getValue();

        assertEquals(API_KEY, clientParameters.getApiKey());
        assertEquals(Collections.singletonList(TOPIC), clientParameters.getTopics());

        assertNotNull(clientParameters.getHostname());
        assertNotNull(clientParameters.getMqttPersistenceDataDir());
        assertTrue(clientParameters.getMessageQos() > 0);
        assertTrue(clientParameters.getKeepAliveIntervalSeconds() > 0);
        assertTrue(clientParameters.getConnectionTimeout() > 0);
    }

    @Test
    void shouldThrowExceptionOnLoApiClientIfPropertiesHasNoTopic() {
        // given
        LoConfig loConfig = new LoConfig(properties, loMqttHandler, loApiClientFactory);

        // when / then
        assertThrows(IllegalArgumentException.class, loConfig::loApiClient);
    }

    @Test
    void shouldCreateLoApiClientBasedOnProvidedLoProperties() {
        // given
        LoProperties customizedLoProperties = getCustomizedLoProperties();
        LoConfig loConfig = new LoConfig(customizedLoProperties, loMqttHandler, loApiClientFactory);

        // when
        loConfig.loApiClient();

        // then
        verify(loApiClientFactory).createLOApiClient(clientParametersCaptor.capture());
        LOApiClientParameters clientParameters = clientParametersCaptor.getValue();

        assertEquals(API_KEY, clientParameters.getApiKey());
        assertEquals(Collections.singletonList(TOPIC), clientParameters.getTopics());

        assertEquals(customizedLoProperties.getHostname(), clientParameters.getHostname());
        assertEquals(customizedLoProperties.getMqttPersistenceDir(), clientParameters.getMqttPersistenceDataDir());
        assertEquals((int) customizedLoProperties.getMessageQos(), clientParameters.getMessageQos());
        assertEquals((int) customizedLoProperties.getKeepAliveIntervalSeconds(), clientParameters.getKeepAliveIntervalSeconds());
        assertEquals((int) customizedLoProperties.getConnectionTimeout(), clientParameters.getConnectionTimeout());
    }

    private LoProperties getMinimumLoProperties() {
        when(properties.getApiKey()).thenReturn(API_KEY);
        when(properties.getTopic()).thenReturn(TOPIC);

        when(properties.getMessageQos()).thenReturn(null);
        when(properties.getKeepAliveIntervalSeconds()).thenReturn(null);
        when(properties.getConnectionTimeout()).thenReturn(null);

        return properties;
    }

    private LoProperties getCustomizedLoProperties() {
        when(properties.getApiKey()).thenReturn(API_KEY);
        when(properties.getTopic()).thenReturn(TOPIC);

        when(properties.getHostname()).thenReturn("custom.hostname");
        when(properties.getMqttPersistenceDir()).thenReturn("/tmp");
        when(properties.getMessageQos()).thenReturn(-1);
        when(properties.getKeepAliveIntervalSeconds()).thenReturn(123);
        when(properties.getConnectionTimeout()).thenReturn(321);

        return properties;
    }
}