/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.sqs;

import com.amazonaws.AmazonClientException;
import com.amazonaws.retry.RetryPolicy.RetryCondition;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.orange.lo.sample.sqs.liveobjects.LoMessage;
import com.orange.lo.sample.sqs.liveobjects.LoProperties;
import com.orange.lo.sample.sqs.utils.Counters;
import com.orange.lo.sdk.LOApiClient;
import io.micrometer.core.instrument.Counter;
import net.jodah.failsafe.RetryPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqsSenderTest {

    private static final int FULL_BATCH_SIZE = 10;
    private static final int PART_BATCH_SIZE = 5;
    private static final String MESSAGE = "message";
    private static final String EXCEPTION_MESSAGE = "EXCEPTION_MESSAGE";

    @Mock
    private AmazonSQS amazonSQS;

    @Mock
    private LoProperties loProperties;

    @Mock
    private LOApiClient loApiClient;

    @Mock
    private Counters counters;

    @Mock
    private Counter mesasageSentAttemptCounter;

    @Mock
    private Counter mesasageSentCounter;

    @Mock
    private Counter mesasageSentAttemptFailedCounter;
    
    @Mock
    private SqsProperties sqsProperties;

    @Mock
    private RetryCondition amazonRetryCondition;

    @Captor
    private ArgumentCaptor<SendMessageBatchRequest> messageBatchRequestCaptor;

    @Captor
    private ArgumentCaptor<Double> successEventCaptor;

    @Mock
    private ThreadPoolExecutor tpe;

    private void stubTPESubmit() {
        when(this.tpe.submit((Runnable) any())).then((x) -> {
            Runnable runnable = x.getArgument(0);
            runnable.run();
            return null;
        });
    }

    @Test
    void shouldPassMessagesBatchToAmazonSQS() {
        when(counters.getMesasageSentCounter()).thenReturn(mesasageSentCounter);
        when(sqsProperties.getQueueUrl()).thenReturn("queueUrl");
        when(counters.getMesasageSentAttemptCounter()).thenReturn(mesasageSentAttemptCounter);
        stubTPESubmit();

        SqsSender sqsSender = getSqsSender(new RetryPolicy<>(), new RetryPolicy<>());
        List<LoMessage> messages = getMessages(PART_BATCH_SIZE);

        sqsSender.send(messages);

        verifyMessagesSentBySQS(1, messages);
        verifyCounters(1, messages.size());
    }

    @Test
    void shouldPassEachMessagesBatchToAmazonSQSSeparately() {
        when(counters.getMesasageSentCounter()).thenReturn(mesasageSentCounter);
        when(sqsProperties.getQueueUrl()).thenReturn("queueUrl");
        when(counters.getMesasageSentAttemptCounter()).thenReturn(mesasageSentAttemptCounter);
        stubTPESubmit();

        List<List<LoMessage>> batches = Arrays.asList(
                getMessages(FULL_BATCH_SIZE),
                getMessages(FULL_BATCH_SIZE),
                getMessages(PART_BATCH_SIZE)
        );
        SqsSender sqsSender = getSqsSender(new RetryPolicy<>(), new RetryPolicy<>());

        for (List<LoMessage> list : batches) {
            sqsSender.send(list);
        }

        List<LoMessage> messages = batches.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        verifyMessagesSentBySQS(3, messages);
        verifyCounters(3, messages.size());
    }

    @Test
    void shouldThrowRetryableAmazonClientExceptionWhenSendMessageBatchThrowsAmazonClientException() {
        // given
        AtomicReference<Throwable> catchedException = new AtomicReference<>();
        RetryPolicy<Void> sendMessageRetryPolicy = new RetryPolicy<Void>().handleIf((e) -> {
            catchedException.set(e);
            return true;
        });

        when(counters.getMesasageSentCounter()).thenReturn(mesasageSentCounter);
        when(counters.getMesasageSentAttemptCounter()).thenReturn(mesasageSentAttemptCounter);
        when(counters.getMesasageSentAttemptFailedCounter()).thenReturn(mesasageSentAttemptFailedCounter);
        
        
        doThrow(new AmazonClientException(EXCEPTION_MESSAGE))
                .doReturn(null)
                .when(amazonSQS).sendMessageBatch(any());
        when(amazonRetryCondition.shouldRetry(any(), any(), anyInt())).thenReturn(true);
        stubTPESubmit();

        SqsSender sqsSender = getSqsSender(sendMessageRetryPolicy, new RetryPolicy<>());
        List<LoMessage> messages = Collections.emptyList();

        // when
        sqsSender.send(messages);

        // then
        assertNotNull(catchedException.get());
        assertTrue(catchedException.get() instanceof RetryableAmazonClientException);
        RetryableAmazonClientException hace = (RetryableAmazonClientException) catchedException.get();
        assertEquals(EXCEPTION_MESSAGE, hace.getMessage());
        assertNotNull(hace.getAmazonClientException());
        assertTrue(hace.isShouldRetry());
    }

    @Test
    void shouldThrowSameExceptionWhenSendMessageBatchThrowsNonAmazonClientException() {
        // given
        AtomicReference<Throwable> catchedException = new AtomicReference<>();
        RetryPolicy<Void> sendMessageRetryPolicy = new RetryPolicy<Void>().handleIf((e) -> {
            catchedException.set(e);
            return true;
        });

        when(counters.getMesasageSentCounter()).thenReturn(mesasageSentCounter);
        when(counters.getMesasageSentAttemptCounter()).thenReturn(mesasageSentAttemptCounter);
        doThrow(new RuntimeException()).doReturn(null).when(amazonSQS).sendMessageBatch(any());
        stubTPESubmit();

        SqsSender sqsSender = getSqsSender(sendMessageRetryPolicy, new RetryPolicy<>());
        List<LoMessage> messages = Collections.emptyList();

        // when
        sqsSender.send(messages);

        // then
        assertNotNull(catchedException.get());
        assertTrue(catchedException.get() instanceof RuntimeException);
    }

    @Test
    void shouldPassRejectedExecutionExceptionToExecuteTaskRetryPolicy() {
        // given
        AtomicReference<Throwable> catchedException = new AtomicReference<>();
        RetryPolicy<Void> executeTaskRetryPolicy = new RetryPolicy<Void>().handleIf((e) -> {
            catchedException.set(e);
            return true;
        });

        doThrow(RejectedExecutionException.class).doReturn(null).when(tpe).submit((Runnable) any());

        SqsSender sqsSender = getSqsSender(new RetryPolicy<>(), executeTaskRetryPolicy);
        List<LoMessage> messages = Collections.emptyList();

        // when
        sqsSender.send(messages);

        // then
        assertNotNull(catchedException.get());
        assertTrue(catchedException.get() instanceof RejectedExecutionException);
    }

    private void verifyMessagesSentBySQS(int wantedNumberOfInvocations, List<LoMessage> messages) {
        verify(amazonSQS, times(wantedNumberOfInvocations)).sendMessageBatch(messageBatchRequestCaptor.capture());
        List<String> messagesSent = toMessageBodyList(messageBatchRequestCaptor.getAllValues());
        assertEquals(messages.size(), messagesSent.size());
        assertTrue(messagesSent.containsAll(messages.stream().map(LoMessage::getMessage).collect(Collectors.toList())));
    }

    private List<String> toMessageBodyList(List<SendMessageBatchRequest> allValues) {
        return allValues.stream()
                .map(SendMessageBatchRequest::getEntries)
                .flatMap(List::stream)
                .map(SendMessageBatchRequestEntry::getMessageBody)
                .collect(Collectors.toList());
    }

    private void verifyCounters(int wantedNumberOfInvocations, int messagesSize) {
        verify(mesasageSentAttemptCounter, times(wantedNumberOfInvocations)).increment(anyDouble());
        verify(mesasageSentCounter, times(wantedNumberOfInvocations)).increment(successEventCaptor.capture());
        Double successSum = successEventCaptor.getAllValues()
                .stream()
                .reduce(0.0, Double::sum);
        assertEquals(messagesSize, successSum.intValue());
    }

    private List<LoMessage> getMessages(int amount) {
        return IntStream.rangeClosed(1, amount).mapToObj(i -> new LoMessage(1,MESSAGE + i)).collect(Collectors.toList());
    }

    private SqsSender getSqsSender(RetryPolicy<Void> sendMessageRetryPolicy, RetryPolicy<Void> executeTaskRetryPolicy) {
        return new SqsSender(
                amazonSQS, sqsProperties, loProperties, tpe, counters, null, sendMessageRetryPolicy, executeTaskRetryPolicy, amazonRetryCondition, loApiClient
        );
    }
}