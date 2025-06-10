/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.liveobjects;

import com.orange.lo.sample.sqs.utils.Counters;
import com.orange.lo.sdk.mqtt.DataManagementReconnectCallback;
import org.springframework.stereotype.Component;

@Component
public class LoMqttReconnectHandler implements DataManagementReconnectCallback {

    private final Counters counters;

    public LoMqttReconnectHandler(Counters counters) {
        this.counters = counters;
    }

    @Override
    public void connectComplete(boolean b, String s) {
        counters.getLoConnectionStatus().set(1);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        counters.getLoConnectionStatus().set(0);
    }
}