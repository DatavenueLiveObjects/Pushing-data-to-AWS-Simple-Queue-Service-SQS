package com.orange.lo.sample.sqs.liveobjects;

import com.orange.lo.sample.sqs.sqs.SqsSender;
import com.orange.lo.sample.sqs.utils.Counters;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MqttHandlerTest {

    @Mock
    private SqsSender sqsSender;
    @Mock
    private Counters counters;
    @Mock
    private Counter counter;
    private Queue<String> messageQueueStub;
    private MqttHandler mqttHandler;

    @BeforeEach
    void setUp() {
        when(counters.mqttEvents()).thenReturn(counter);
        this.messageQueueStub = new ConcurrentLinkedQueue<>();
        this.mqttHandler = new MqttHandler(sqsSender, counters, messageQueueStub);
    }

    @Test
    void shouldAddMessageToQueueAndIncrementReceivedEventsCountWhenMessageArrive() {
        Message<String> message = new GenericMessage<>("{}");

        mqttHandler.handleMessage(message);

        assertEquals(1, messageQueueStub.size());
        verify(counter, times(1)).increment();
    }

    @Test
    void shouldNotCallSqsSenderWhenMessageQueueIsEmpt() {
        mqttHandler.send();

        assertEquals(0, messageQueueStub.size());
        verify(sqsSender, times(0)).send(anyList());
    }

    @Test
    void shouldCallSqsSenderAndSendAllMessagesWhenMessageQueueIsNoTEmpty() {
        Message<String> message = new GenericMessage<>("{}");

        mqttHandler.handleMessage(message);
        mqttHandler.send();

        assertEquals(0, messageQueueStub.size());
        verify(sqsSender, times(1)).send(anyList());
    }
}