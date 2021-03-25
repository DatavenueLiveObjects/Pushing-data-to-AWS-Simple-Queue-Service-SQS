package com.orange.lo.sample.sqs.liveobjects;


import com.orange.lo.sample.sqs.utils.Counters;
import com.orange.lo.sdk.fifomqtt.DataManagementFifoCallback;
import io.micrometer.core.instrument.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Queue;

@Component
public class LoMqttHandler implements DataManagementFifoCallback {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Counter mqttEvtCounter;
    private final Queue<String> messageQueue;

    public LoMqttHandler(Counters counterProvider, Queue<String> messageQueue) {
        LOG.info("LoMqttHandler init...");

        this.mqttEvtCounter = counterProvider.mqttEvents();
        this.messageQueue = messageQueue;
    }

    @Override
    public void onMessage(String message) {
        mqttEvtCounter.increment();
        messageQueue.add(message);
    }
}