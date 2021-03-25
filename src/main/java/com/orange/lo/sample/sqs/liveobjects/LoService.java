package com.orange.lo.sample.sqs.liveobjects;


import com.google.common.collect.Lists;
import com.orange.lo.sample.sqs.sqs.SqsSender;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Queue;

@Component
public class LoService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final SqsSender sqsSender;
    private final Queue<String> messageQueue;
    private final DataManagementFifo dataManagementFifo;

    public LoService(LOApiClient loApiClient, SqsSender sqsSender, Queue<String> messageQueue) {
        LOG.info("LoService init...");

        this.sqsSender = sqsSender;
        this.messageQueue = messageQueue;
        this.dataManagementFifo = loApiClient.getDataManagementFifo();
        ;
    }

    public void start() {
        dataManagementFifo.connectAndSubscribe();
    }

    public void stop() {
        dataManagementFifo.disconnect();
    }

    @Scheduled(fixedRateString = "${lo.synchronization-interval}")
    public void send() {
        if (!messageQueue.isEmpty()) {
            LOG.info("Start retriving messages...");
            List<String> messageBatch = Lists.newArrayListWithCapacity(10);
            while (!messageQueue.isEmpty()) {
                messageBatch.add(messageQueue.poll());
                if (messageBatch.size() == 10) {
                    sqsSender.send(Lists.newArrayList(messageBatch));
                    messageBatch.clear();
                }
            }

            if (!messageBatch.isEmpty())
                sqsSender.send(Lists.newArrayList(messageBatch));
        }
    }
}