/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.utils;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class ConnectorHealthActuatorEndpoint implements HealthIndicator {

    Counters counters;

    public ConnectorHealthActuatorEndpoint(Counters counters) {
        this.counters = counters;
    }

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder(Status.UP);

        builder.withDetail("loMqttConnectionStatus", isLoConnectionStatus());
        builder.withDetail("cloudConnectionStatus", isCloudConnectionStatus());
        return builder.build();
    }

    public boolean isCloudConnectionStatus() {
        return counters.getCloudConnectionStatus().get() > 0;
    }

    public boolean isLoConnectionStatus() {
        return counters.getLoConnectionStatus().get() > 0;
    }
}