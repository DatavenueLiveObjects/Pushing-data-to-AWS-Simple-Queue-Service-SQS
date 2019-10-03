/** 
* Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved. 
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.sqs.sqs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;

import java.lang.invoke.MethodHandles;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Controller
public class ApiController {

    private Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SqsSender sqsSender;
    private MessageProducerSupport mqttInbound;
    private Queue<String> messageQueue;

    @Autowired
    public ApiController(SqsSender sqsSender, MessageProducerSupport mqttInbound) {
        log.info("ApiController init...");
        this.sqsSender = sqsSender;
        this.mqttInbound = mqttInbound;
        messageQueue = new ConcurrentLinkedQueue<>();
    }

    @GetMapping(path="/start")
    public ResponseEntity<String> startMqtt() {
        log.info("STARTING MQTT");
        mqttInbound.start();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path="/stop")
    public ResponseEntity<String> stopMqtt() {
        log.info("STOPPING MQTT");
        mqttInbound.stop(() -> log.info("STOPPED"));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path="/testsend")
    public ResponseEntity<String> sendMessageToSqs(@RequestParam(name = "count", defaultValue = "1") int messageCount) {
        log.info("Store in queue start ({})...", messageCount);
        for (int i = 1; i <= messageCount; i++) {
            messageQueue.add(String.format("{ test: \"%d\" }", i));
        }
        log.info("...store complete");
        return new ResponseEntity<>(HttpStatus.OK);
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