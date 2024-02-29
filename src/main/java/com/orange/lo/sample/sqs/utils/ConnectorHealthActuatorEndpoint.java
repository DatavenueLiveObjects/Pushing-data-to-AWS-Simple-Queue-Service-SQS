package com.orange.lo.sample.sqs.utils;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifo;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class ConnectorHealthActuatorEndpoint implements HealthIndicator {

    LOApiClient loApiClient;
    boolean cloudConnectionStatus = true;
    boolean loConnectionStatus = true;

    public ConnectorHealthActuatorEndpoint(LOApiClient loApiClient) {
        this.loApiClient = loApiClient;
    }

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder(Status.UP);
        DataManagementFifo dataManagementFifo = loApiClient.getDataManagementFifo();

        builder.withDetail("loMqttConnectionStatus", dataManagementFifo.isConnected() || loConnectionStatus);
        builder.withDetail("cloudConnectionStatus", cloudConnectionStatus);
        return builder.build();
    }


    public void setCloudConnectionStatus(boolean cloudConnectionStatus) {
        this.cloudConnectionStatus = cloudConnectionStatus;
    }

    public boolean isCloudConnectionStatus() {
        return cloudConnectionStatus;
    }

    public void setLoConnectionStatus(boolean cloudConnectionStatus) {
        this.loConnectionStatus = cloudConnectionStatus;
    }

    public boolean isLoConnectionStatus() {
        return loConnectionStatus;
    }

}
