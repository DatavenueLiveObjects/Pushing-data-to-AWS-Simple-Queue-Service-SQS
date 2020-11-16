/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.sqs.liveobjects;

import com.google.common.collect.Lists;
import com.orange.lo.sample.sqs.utils.Counters;
import com.orange.lo.sample.sqs.sqs.SqsSender;

import io.micrometer.core.instrument.Counter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

import java.util.List;
import java.util.Queue;

@Component
public class MqttHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private SqsSender sqsSender;
    private Counter mqttEvtCounter;
    private Queue<String> messageQueue;

    public MqttHandler(SqsSender sqsSender, Counters counterProvider, Queue<String> messageQueue) {
        LOG.info("MqttHandler init...");
        this.sqsSender = sqsSender;
        this.mqttEvtCounter = counterProvider.mqttEvents();
        this.messageQueue = messageQueue;
    }

    public void handleMessage(Message<String> message) {
        mqttEvtCounter.increment();
        messageQueue.add(message.getPayload());
    }

    @Scheduled(fixedDelay = 1000)
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