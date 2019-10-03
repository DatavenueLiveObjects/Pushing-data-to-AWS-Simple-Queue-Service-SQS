/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.mqtt2eventhub.evthub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.invoke.MethodHandles;

@Controller
public class ApiController {
    private Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EvtHubSender evtHubSender;

    @Autowired
    public ApiController(EvtHubSender evtHubSender) {
        this.evtHubSender = evtHubSender;
    }

    @GetMapping(path="/testsend")
    public ResponseEntity<String> sendMessageToEventHub(@RequestParam(name = "count", defaultValue = "1") int messageCount) {

        log.info("send start ({})...", messageCount);
        for (int i = 0; i < messageCount; i++) {
            evtHubSender.send(String.format("{ test: \"%d\" }", i));
        }
        log.info("...send complete");

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
