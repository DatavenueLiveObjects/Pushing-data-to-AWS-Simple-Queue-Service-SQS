/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.sqs.liveobjects;

import com.google.common.collect.Lists;
import com.orange.lo.sample.sqs.Counters;
import com.orange.lo.sample.sqs.sqs.SqsSender;

import io.micrometer.core.instrument.Counter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class MqttHandler {

    private Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private SqsSender sqsSender;
    private Counter mqttEvtCounter;
    private Queue<String> messageQueue;

    @Autowired
    public MqttHandler(SqsSender sqsSender, Counters counterProvider) {
        log.info("MqttHandler init...");
        this.sqsSender = sqsSender;
        mqttEvtCounter = counterProvider.mqttEvents();
        messageQueue = new ConcurrentLinkedQueue<>();
    }

    public void handleMessage(Message<String> message) {
        mqttEvtCounter.increment();
        messageQueue.add(message.getPayload());
    }

    @Scheduled(fixedDelay = 1000)
    public void send() {
        if (!messageQueue.isEmpty()) {
            log.info("Start retriving messages...");
            List<String> messageBatch = Lists.newArrayListWithCapacity(10);
            while (!messageQueue.isEmpty()) {
                messageBatch.add(messageQueue.poll());
                if (messageBatch.size() == 10) {
                    sqsSender.send(Lists.newArrayList(messageBatch));
                    messageBatch.clear();
                }
            }
            if (messageBatch.size() > 0)
                sqsSender.send(Lists.newArrayList(messageBatch));
        }
    }

}