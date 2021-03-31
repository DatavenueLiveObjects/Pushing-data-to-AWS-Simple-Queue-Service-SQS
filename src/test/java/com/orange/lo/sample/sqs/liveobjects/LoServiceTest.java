package com.orange.lo.sample.sqs.liveobjects;

import com.orange.lo.sample.sqs.sqs.SqsSender;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoServiceTest {

    @Mock
    LOApiClient loApiClient;

    @Mock
    SqsSender sqsSender;

    @Mock
    DataManagementFifo dataManagementFifo;

    @Mock
    private LoProperties properties;

    private LinkedList<String> messageQueue;

    private LoService service;


    @BeforeEach
    void setUp() {
        when(loApiClient.getDataManagementFifo()).thenReturn(dataManagementFifo);
        messageQueue = new LinkedList<>();
        service = new LoService(loApiClient, sqsSender, messageQueue, properties);
    }

    @Test
    public void shouldStartMethodDoTriggerDataManagementFifo() {
        // when
        service.start();

        // then
        verify(dataManagementFifo, times(1)).connectAndSubscribe();
    }

    @Test
    public void shouldStopMethodDoDisconnectDataManagementFifo() {
        // when
        service.stop();

        // then
        verify(dataManagementFifo, times(1)).disconnect();
    }

    @Test
    public void shouldNotSendMessagesIfQueueIsEmpty() {
        // when
        service.send();

        // then
        verify(sqsSender, never()).send(any());
    }

    @Test
    public void shouldSendMessagesInOneBatchIfQueueNotExceedMessageBatchSizeProperty() {
        // given
        int batchSize = 5;

        when(properties.getMessageBatchSize()).thenReturn(batchSize);
        service = new LoService(loApiClient, sqsSender, messageQueue, properties);

        // TODO: Replace with stream
        for (int i = 0; i < batchSize; i++) {
            messageQueue.push(String.format("Message %d", i + 1));
        }
        List<String> expectedMessages = new ArrayList<>(messageQueue);

        // when
        service.send();

        // then
        verify(sqsSender, times(1)).send(expectedMessages);
    }

    @Test
    public void shouldSplitMessagesIntoPacketsIfQueueExceedMessageBatchSizeProperty() {
        // given
        int batchSize = 5;
        int totalLength = batchSize + 1;

        when(properties.getMessageBatchSize()).thenReturn(batchSize);
        service = new LoService(loApiClient, sqsSender, messageQueue, properties);

        // TODO: Replace with stream
        for (int i = 0; i < totalLength; i++) {
            messageQueue.push(String.format("Message %d", i + 1));
        }
        List<String> expectedMessages1 = (new LinkedList<>(messageQueue)).subList(0, batchSize);
        List<String> expectedMessages2 = (new LinkedList<>(messageQueue)).subList(batchSize, totalLength);

        // when
        service.send();

        // then
        verify(sqsSender, times(1)).send(expectedMessages1);
        verify(sqsSender, times(1)).send(expectedMessages2);
    }

    @Test
    public void shouldSetDefaultBatchSizeTo10() {
        // given
        int expectedBatchSize = 10;

        // TODO: Replace with stream
        for (int i = 0; i < expectedBatchSize; i++) {
            messageQueue.push(String.format("Message %d", i + 1));
        }
        List<String> expectedMessages = (new LinkedList<>(messageQueue)).subList(0, expectedBatchSize);

        // when
        service.send();

        // then
        verify(sqsSender, times(1)).send(expectedMessages);
        verify(sqsSender, never()).send(not(eq(expectedMessages)));
    }
}