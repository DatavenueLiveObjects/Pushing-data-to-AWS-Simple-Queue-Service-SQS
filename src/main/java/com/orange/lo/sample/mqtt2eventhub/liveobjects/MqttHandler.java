/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sample.mqtt2eventhub.Counters;
import com.orange.lo.sample.mqtt2eventhub.evthub.EvtHubSender;
import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class MqttHandler {
    private EvtHubSender evtHubSender;
    private Counter mqttEvtCounter;

    @Autowired
    public MqttHandler(EvtHubSender evtHubSender, Counters counterProvider) {
        this.evtHubSender = evtHubSender;
        mqttEvtCounter = counterProvider.mqttEvents();
    }

    public void handleMessage(Message<String> msg) {
        mqttEvtCounter.increment();
        evtHubSender.send(msg.getPayload());
    }

}
