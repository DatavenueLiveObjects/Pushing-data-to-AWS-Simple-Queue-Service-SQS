/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.liveobjects;


import com.orange.lo.sample.sqs.sqs.SqsSender;
import com.orange.lo.sample.sqs.utils.ConnectorHealthActuatorEndpoint;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Mock
    private ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint;

    private LoService service;


    @BeforeEach
    void setUp() {
        when(loApiClient.getDataManagementFifo()).thenReturn(dataManagementFifo);
        prepareService(new LinkedList<>());
    }

    private void prepareService(LinkedList<LoMessage> messageQueue) {
        service = new LoService(loApiClient, sqsSender, messageQueue, properties, connectorHealthActuatorEndpoint);
    }

    @Test
    void shouldStartMethodDoTriggerDataManagementFifo() {
        when(connectorHealthActuatorEndpoint.isLoConnectionStatus()).thenReturn(true);
        when(connectorHealthActuatorEndpoint.isCloudConnectionStatus()).thenReturn(true);

        // when
        service.start();

        // then
        verify(dataManagementFifo, times(1)).connectAndSubscribe();
    }

    @Test
    void shouldStopMethodDoDisconnectDataManagementFifo() {
        // when
        service.stop();

        // then
        verify(dataManagementFifo, times(1)).disconnect();
    }

    @Test
    void shouldNotSendMessagesIfQueueIsEmpty() {
        // when
        service.send();

        // then
        verify(sqsSender, never()).send(any());
    }

    @Test
    void shouldSendMessagesInOneBatchIfQueueNotExceedMessageBatchSizeProperty() {
        // given
        int batchSize = 5;

        when(properties.getMessageBatchSize()).thenReturn(batchSize);

        LinkedList<LoMessage> messageQueue = getExampleMessageQueue(batchSize);

        prepareService(messageQueue);

        List<LoMessage> expectedMessages = new ArrayList<>(messageQueue);

        // when
        service.send();

        // then
        verify(sqsSender, times(1)).send(expectedMessages);
    }

    @Test
    void shouldSplitMessagesIntoPacketsIfQueueExceedMessageBatchSizeProperty() {
        // given
        int batchSize = 5;
        int totalLength = batchSize + 1;

        when(properties.getMessageBatchSize()).thenReturn(batchSize);

        LinkedList<LoMessage> messageQueue = getExampleMessageQueue(totalLength);

        prepareService(messageQueue);

        List<LoMessage> expectedMessages1 = (new LinkedList<>(messageQueue)).subList(0, batchSize);
        List<LoMessage> expectedMessages2 = (new LinkedList<>(messageQueue)).subList(batchSize, totalLength);

        // when
        service.send();

        // then
        verify(sqsSender, times(1)).send(expectedMessages1);
        verify(sqsSender, times(1)).send(expectedMessages2);
    }

    @Test
    void shouldSetDefaultBatchSizeTo10() {
        // given
        int expectedBatchSize = 10;

        LinkedList<LoMessage> messageQueue = getExampleMessageQueue(expectedBatchSize);

        prepareService(messageQueue);

        List<LoMessage> expectedMessages = (new LinkedList<>(messageQueue)).subList(0, expectedBatchSize);

        // when
        service.send();

        // then
        verify(sqsSender, times(1)).send(expectedMessages);
        verify(sqsSender, never()).send(not(eq(expectedMessages)));
    }

    private LinkedList<LoMessage> getExampleMessageQueue(int batchSize) {
        return IntStream.range(1, batchSize + 1)
                .mapToObj(i -> new LoMessage(i, String.format("Message %d", i)))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}