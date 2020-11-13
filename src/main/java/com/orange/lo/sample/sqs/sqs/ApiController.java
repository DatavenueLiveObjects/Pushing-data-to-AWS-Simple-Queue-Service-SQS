/** 
* Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved. 
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.sqs.sqs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.invoke.MethodHandles;

@Controller
public class ApiController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageProducerSupport mqttInbound;

    public ApiController(MessageProducerSupport mqttInbound) {
        LOG.info("ApiController init...");
        this.mqttInbound = mqttInbound;
    }

    @GetMapping(path = "/start")
    public ResponseEntity<String> startMqtt() {
        LOG.info("STARTING MQTT");
        mqttInbound.start();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/stop")
    public ResponseEntity<String> stopMqtt() {
        LOG.info("STOPPING MQTT");
        mqttInbound.stop(() -> LOG.info("STOPPED"));
        return new ResponseEntity<>(HttpStatus.OK);
    }

}