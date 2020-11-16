package com.orange.lo.sample.sqs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationConfigTest {

    private ApplicationConfig applicationConfig;

    @BeforeEach
    void setUp() {
        applicationConfig = new ApplicationConfig();
    }

    @Test
    void shouldCreateMessageQueue() {
        Queue<String> stringQueue = applicationConfig.messageQueue();

        assertNotNull(stringQueue);
    }
}