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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqsSenderTest {

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

        SqsProperties sqsProperties = new SqsProperties();
        sqsProperties.setQueueUrl("queueUrl");
        sqsProperties.setThreadPoolSize(20);
        sqsProperties.setTaskQueueSize(20);
        BlockingQueue<Runnable> tasks = new ArrayBlockingQueue<>(sqsProperties.getTaskQueueSize());
        this.tpe = new ThreadPoolExecutor(sqsProperties.getThreadPoolSize(),
                sqsProperties.getThreadPoolSize(), 10, TimeUnit.SECONDS, tasks);
        this.sqsSender = new SqsSender(amazonSQS, sqsProperties, tpe, counters);
    }

    @Test
    void shouldPassMessagesBatchToAmazonSQS() throws InterruptedException {
        List<String> messages = getMessages(PART_BATCH_SIZE);

        sqsSender.send(messages);
        tpe.awaitTermination(3, TimeUnit.SECONDS);

        verify(amazonSQS, times(1)).sendMessageBatch(messageBatchRequestCaptor.capture());
        List<String> messagesSent = toMessageBodyList(messageBatchRequestCaptor.getAllValues());
        assertEquals(messages.size(), messagesSent.size());
        assertTrue(messagesSent.containsAll(messages));

        verify(evtAttemptCounter, times(1)).increment();
        verify(evtSuccess, times(1)).increment(PART_BATCH_SIZE);
    }

    @Test
    void shouldPassEachMessagesBatchToAmazonSQSSeparately() throws InterruptedException {
        List<List<String>> lists = Arrays.asList(
                getMessages(FULL_BATCH_SIZE),
                getMessages(FULL_BATCH_SIZE),
                getMessages(PART_BATCH_SIZE)
        );

        for (List<String> list : lists) {
            sqsSender.send(list);
        }
        tpe.awaitTermination(3, TimeUnit.SECONDS);

        verify(amazonSQS, times(3)).sendMessageBatch(messageBatchRequestCaptor.capture());
        List<String> messagesSent = toMessageBodyList(messageBatchRequestCaptor.getAllValues());
        List<String> messages = lists.stream().flatMap(List::stream)
                .collect(Collectors.toList());
        assertEquals(messages.size(), messagesSent.size());
        assertTrue(messagesSent.containsAll(messages));

        verify(evtAttemptCounter, times(3)).increment();
        verify(evtSuccess, times(3)).increment(successEventCaptor.capture());
        Double successSum = successEventCaptor.getAllValues()
                .stream()
                .reduce(0.0, Double::sum);
        assertEquals(messages.size(), successSum.intValue());
    }

    private List<String> toMessageBodyList(List<SendMessageBatchRequest> allValues) {
        return allValues.stream()
                .map(SendMessageBatchRequest::getEntries)
                .flatMap(List::stream)
                .map(SendMessageBatchRequestEntry::getMessageBody)
                .collect(Collectors.toList());
    }

    private List<String> getMessages(int amount) {
        return IntStream.rangeClosed(1, amount).mapToObj(i -> message + i).collect(Collectors.toList());
    }

}