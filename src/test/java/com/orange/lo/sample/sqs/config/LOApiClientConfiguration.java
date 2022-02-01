package com.orange.lo.sample.sqs.config;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifo;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class LOApiClientConfiguration {

    @Bean
    @Primary
    public LOApiClient testLoApiClient() {
        DataManagementFifo dataManagementFifo = Mockito.mock(DataManagementFifo.class);
        LOApiClient loApiClient = Mockito.mock(LOApiClient.class);
        Mockito.when(loApiClient.getDataManagementFifo()).thenReturn(dataManagementFifo);
        return loApiClient;
    }
}