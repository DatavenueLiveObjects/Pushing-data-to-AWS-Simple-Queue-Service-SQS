package com.orange.lo.sample.sqs.sqs;

import com.orange.lo.sample.sqs.utils.Counters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SqsClientConfigTest {

    @Mock
    private Counters counters;
    private SqsClientConfig sqsClientConfig;

    @BeforeEach
    void setUp() {
        SqsProperties sqsProperties = new SqsProperties();
        sqsProperties.setThreadPoolSize(20);
        sqsProperties.setTaskQueueSize(20);
        sqsClientConfig = new SqsClientConfig(sqsProperties, counters);
    }

    @Test
    void sqsSender() {
        SqsSender sqsSender = sqsClientConfig.sqsSender();

        assertNotNull(sqsSender);
    }
}