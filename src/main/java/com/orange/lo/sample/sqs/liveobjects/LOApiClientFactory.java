package com.orange.lo.sample.sqs.liveobjects;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.LOApiClientParameters;

public interface LOApiClientFactory {
    LOApiClient createLOApiClient(LOApiClientParameters parameters);
}
