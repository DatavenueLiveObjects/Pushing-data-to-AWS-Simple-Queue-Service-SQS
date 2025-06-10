package com.orange.lo.sample.sqs.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConnectorHealthActuatorEndpointTest {

    @Mock
    private Counters counters;
    private ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint;


    @BeforeEach
    void setUp() {
        when(counters.getLoConnectionStatus()).thenReturn(new AtomicInteger());
        when(counters.getCloudConnectionStatus()).thenReturn( new AtomicInteger());
        this.connectorHealthActuatorEndpoint = new ConnectorHealthActuatorEndpoint(counters);
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void checkCloudConnectionStatus(boolean isConnected) {
        // when
        counters.getCloudConnectionStatus().set(isConnected ? 1 : 0);
        boolean cloudConnectionStatus = (boolean) connectorHealthActuatorEndpoint.health().getDetails()
                .get("cloudConnectionStatus");

        // then
        assertEquals(isConnected, cloudConnectionStatus);
        assertEquals(isConnected, connectorHealthActuatorEndpoint.isCloudConnectionStatus());
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void checkLoConnectionStatus(boolean isConnected) {
        // when
        counters.getLoConnectionStatus().set(isConnected ? 1 : 0);
        boolean loMqttConnectionStatus = (boolean) connectorHealthActuatorEndpoint.health().getDetails()
                .get("loMqttConnectionStatus");

        // then
        assertEquals(isConnected, loMqttConnectionStatus);
        assertEquals(isConnected, connectorHealthActuatorEndpoint.isLoConnectionStatus());
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void checkHealth(boolean includeDetails) {
        // when
        Health health = connectorHealthActuatorEndpoint.getHealth(includeDetails);
        Map<String, Object> details = health.getDetails();
        int expectedDetailsSize = includeDetails ? 2 : 0;

        // then
        assertEquals(expectedDetailsSize, details.size());
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(Arguments.of(true),
                Arguments.of(false));
    }

}