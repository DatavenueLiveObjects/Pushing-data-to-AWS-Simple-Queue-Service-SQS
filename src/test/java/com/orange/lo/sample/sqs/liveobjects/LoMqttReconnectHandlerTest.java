package com.orange.lo.sample.sqs.liveobjects;

import com.orange.lo.sample.sqs.utils.Counters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoMqttReconnectHandlerTest {

    @Mock
    private Counters counters;
    private LoMqttReconnectHandler loMqttReconnectHandler;

    @BeforeEach
    void setUp() {
        this.loMqttReconnectHandler = new LoMqttReconnectHandler(counters);
    }

    @Test
    void shouldChangeLoConnectionStausWhenConnectComplete() {
        AtomicInteger loConnectionStatus = new AtomicInteger(0);
        when(counters.getLoConnectionStatus()).thenReturn(loConnectionStatus);

        loMqttReconnectHandler.connectComplete(false, "");

        verify(counters, times(1)).getLoConnectionStatus();
        assertEquals(1, loConnectionStatus.get());
    }

    @Test
    void shouldChangeLoConnectionStausWhenConnectionLost() {
        AtomicInteger loConnectionStatus = new AtomicInteger(1);
        when(counters.getLoConnectionStatus()).thenReturn(loConnectionStatus);

        loMqttReconnectHandler.connectionLost(new Exception());

        verify(counters, times(1)).getLoConnectionStatus();
        assertEquals(0, loConnectionStatus.get());
    }
}