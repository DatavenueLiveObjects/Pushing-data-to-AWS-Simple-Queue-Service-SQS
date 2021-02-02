package com.orange.lo.sample.sqs.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.orange.lo.sample.sqs.utils.Counters;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.AfterEach;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqsSenderTest {

    public static final int PART_BATCH_SIZE = 5;
    private static final String message = "message";
    public static final int FULL_BATCH_SIZE = 10;

    @Mock
    private AmazonSQS amazonSQS;
    @Mock
    private Counters counters;
    @Mock
    private Counter evtAttemptCounter;
    @Mock
    private Counter evtSuccess;
    @Captor
    private ArgumentCaptor<SendMessageBatchRequest> captor;
    private SqsSender sqsSender;

    @BeforeEach
    void setUp() {
        when(counters.evtAttemptCount()).thenReturn(evtAttemptCounter);
        when(counters.evtSuccess()).thenReturn(evtSuccess);

        SqsProperties sqsProperties = new SqsProperties();
        sqsProperties.setQueueUrl("queueUrl");
        sqsProperties.setThreadPoolSize(20);
        sqsProperties.setTaskQueueSize(20);
        BlockingQueue<Runnable> tasks = new ArrayBlockingQueue<>(sqsProperties.getTaskQueueSize());
        ThreadPoolExecutor tpe = new ThreadPoolExecutor(sqsProperties.getThreadPoolSize(),
                sqsProperties.getThreadPoolSize(), 10, TimeUnit.SECONDS, tasks);
        this.sqsSender = new SqsSender(amazonSQS, sqsProperties, tpe, counters);
    }

    @Test
    void shouldPassMessagesBatchToAmazonSQS() throws InterruptedException {
        List<String> messages = getMessages(PART_BATCH_SIZE);
        sendMessagesBatch(messages);

        verify(amazonSQS, times(1)).sendMessageBatch(captor.capture());
        List<SendMessageBatchRequest> allValues = captor.getAllValues();
        for (SendMessageBatchRequest batchRequest : allValues) {
            List<SendMessageBatchRequestEntry> entries = batchRequest.getEntries();
            assertEquals(messages.size(), entries.size());
        }
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
            sendMessagesBatch(list);
        }

        verify(amazonSQS, times(3)).sendMessageBatch(captor.capture());
        List<SendMessageBatchRequest> allValues = captor.getAllValues();

        for (int i = 0; i < allValues.size(); i++) {
            SendMessageBatchRequest batchRequest = allValues.get(i);
            List<String> strings = lists.get(i);
            List<SendMessageBatchRequestEntry> entries = batchRequest.getEntries();
            assertEquals(strings.size(), entries.size());
        }
        verify(evtAttemptCounter, times(3)).increment();
        verify(evtSuccess, times(2)).increment(FULL_BATCH_SIZE);
        verify(evtSuccess, times(1)).increment(PART_BATCH_SIZE);
    }

    private void sendMessagesBatch(List<String> messages) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        doAnswer(invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(amazonSQS).sendMessageBatch(any(SendMessageBatchRequest.class));

        sqsSender.send(messages);
        countDownLatch.await(5, TimeUnit.SECONDS);
    }

    private List<String> getMessages(int amount) {
        return IntStream.rangeClosed(1, amount).mapToObj(i -> message + i).collect(Collectors.toList());
    }

    @AfterEach
    public void showInteractions() {
        System.out.println(mockingDetails(evtSuccess).getInvocations());
        System.out.println(mockingDetails(evtAttemptCounter).getInvocations());
        System.out.println(mockingDetails(amazonSQS).getInvocations());
    }
}