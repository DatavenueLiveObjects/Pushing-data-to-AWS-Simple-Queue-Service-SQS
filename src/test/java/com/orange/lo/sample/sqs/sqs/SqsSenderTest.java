package com.orange.lo.sample.sqs.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.orange.lo.sample.sqs.utils.Counters;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqsSenderTest {

    public static final int TIMEOUT = 3;
    private static final int FULL_BATCH_SIZE = 10;
    private static final int PART_BATCH_SIZE = 5;
    private static final String message = "message";

    @Mock
    private AmazonSQS amazonSQS;

    @Mock
    private Counters counters;

    @Mock
    private Counter evtAttemptCounter;

    @Mock
    private Counter evtSuccess;

    @Mock
    private SqsProperties sqsProperties;

    @Captor
    private ArgumentCaptor<SendMessageBatchRequest> messageBatchRequestCaptor;

    @Captor
    private ArgumentCaptor<Double> successEventCaptor;

    private SqsSender sqsSender;
    private ThreadPoolExecutor tpe;

    @BeforeEach
    void setUp() {
        when(counters.evtAttemptCount()).thenReturn(evtAttemptCounter);
        when(counters.evtSuccess()).thenReturn(evtSuccess);

        when(sqsProperties.getQueueUrl()).thenReturn("queueUrl");
        when(sqsProperties.getThreadPoolSize()).thenReturn(20);
        when(sqsProperties.getTaskQueueSize()).thenReturn(20);
        BlockingQueue<Runnable> tasks = new ArrayBlockingQueue<>(sqsProperties.getTaskQueueSize());
        this.tpe = new ThreadPoolExecutor(sqsProperties.getThreadPoolSize(),
                sqsProperties.getThreadPoolSize(), 10, TimeUnit.SECONDS, tasks);
        this.sqsSender = new SqsSender(amazonSQS, sqsProperties, tpe, counters);
    }

    @Test
    void shouldPassMessagesBatchToAmazonSQS() throws InterruptedException {
        List<String> messages = getMessages(PART_BATCH_SIZE);

        sqsSender.send(messages);
        tpe.awaitTermination(TIMEOUT, TimeUnit.SECONDS);

        verifyMessages(1, messages);
        verifyCounters(1, messages.size());
    }

    @Test
    void shouldPassEachMessagesBatchToAmazonSQSSeparately() throws InterruptedException {
        List<List<String>> batches = Arrays.asList(
                getMessages(FULL_BATCH_SIZE),
                getMessages(FULL_BATCH_SIZE),
                getMessages(PART_BATCH_SIZE)
        );

        for (List<String> list : batches) {
            sqsSender.send(list);
        }
        tpe.awaitTermination(TIMEOUT, TimeUnit.SECONDS);

        List<String> messages = batches.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        verifyMessages(3, messages);
        verifyCounters(3, messages.size());
    }

    private void verifyMessages(int wantedNumberOfInvocations, List<String> messages) {
        verify(amazonSQS, times(wantedNumberOfInvocations)).sendMessageBatch(messageBatchRequestCaptor.capture());
        List<String> messagesSent = toMessageBodyList(messageBatchRequestCaptor.getAllValues());
        assertEquals(messages.size(), messagesSent.size());
        assertTrue(messagesSent.containsAll(messages));
    }

    private List<String> toMessageBodyList(List<SendMessageBatchRequest> allValues) {
        return allValues.stream()
                .map(SendMessageBatchRequest::getEntries)
                .flatMap(List::stream)
                .map(SendMessageBatchRequestEntry::getMessageBody)
                .collect(Collectors.toList());
    }

    private void verifyCounters(int wantedNumberOfInvocations, int messagesSize) {
        verify(evtAttemptCounter, times(wantedNumberOfInvocations)).increment();
        verify(evtSuccess, times(wantedNumberOfInvocations)).increment(successEventCaptor.capture());
        Double successSum = successEventCaptor.getAllValues()
                .stream()
                .reduce(0.0, Double::sum);
        assertEquals(messagesSize, successSum.intValue());
    }

    private List<String> getMessages(int amount) {
        return IntStream.rangeClosed(1, amount).mapToObj(i -> message + i).collect(Collectors.toList());
    }
}