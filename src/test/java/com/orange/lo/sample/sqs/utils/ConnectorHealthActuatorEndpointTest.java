package com.orange.lo.sample.sqs.utils;

import com.orange.lo.sample.sqs.config.LOApiClientConfiguration;
import com.orange.lo.sdk.LOApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(classes = {LOApiClientConfiguration.class})
class ConnectorHealthActuatorEndpointTest {

    @Autowired
    public LOApiClient loApiClient;
    private ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint;


    @BeforeEach
    void setUp() {
        this.connectorHealthActuatorEndpoint = new ConnectorHealthActuatorEndpoint(loApiClient);
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void checkCloudConnectionStatus(boolean isConnected) {
        // when
        connectorHealthActuatorEndpoint.setCloudConnectionStatus(isConnected);
        boolean cloudConnectionStatus = (boolean) connectorHealthActuatorEndpoint.health().getDetails()
                .get("cloudConnectionStatus");

        // then
        assertEquals(cloudConnectionStatus, isConnected);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(Arguments.of(true),
                Arguments.of(false));
    }


}