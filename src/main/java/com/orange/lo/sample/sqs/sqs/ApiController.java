/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.sqs;

import com.orange.lo.sample.sqs.liveobjects.LoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.invoke.MethodHandles;

@Controller
public class ApiController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LoService loService;

    public ApiController(LoService loService) {
        LOG.info("ApiController init...");
        this.loService = loService;
    }

    @GetMapping(path = "/start")
    public ResponseEntity<String> startMqtt() {
        LOG.info("STARTING MQTT");
        loService.start();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/stop")
    public ResponseEntity<String> stopMqtt() {
        LOG.info("STOPPING MQTT");
        loService.stop();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}