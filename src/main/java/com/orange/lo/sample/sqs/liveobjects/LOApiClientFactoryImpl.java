package com.orange.lo.sample.sqs.liveobjects;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.LOApiClientParameters;
import org.springframework.stereotype.Component;

@Component
public class LOApiClientFactoryImpl implements LOApiClientFactory {
    @Override
    public LOApiClient createLOApiClient(LOApiClientParameters parameters) {
        return new LOApiClient(parameters);
    }
}
