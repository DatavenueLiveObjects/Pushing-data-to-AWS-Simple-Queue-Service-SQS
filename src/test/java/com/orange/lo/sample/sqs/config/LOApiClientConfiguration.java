/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.config;

import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifo;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

@TestConfiguration
public class LOApiClientConfiguration {

    @TempDir
    File tempDir;

    @PostConstruct
    public void init() throws IOException {
        File config = new File(tempDir, "config");
        System.setProperty("aws.region", "eu-west-1");
        System.setProperty("aws.profile", "service-profile");
        System.setProperty("aws.configFile", config.getAbsolutePath());
        List<String> lines = Arrays.asList("[profile service-profile]", "region=eu-west-1");
        Files.write(config.toPath(), lines);
    }

    @Bean
    public LOApiClient loApiClient() {
        DataManagementFifo dataManagementFifo = Mockito.mock(DataManagementFifo.class);
        LOApiClient loApiClient = Mockito.mock(LOApiClient.class);
        Mockito.when(loApiClient.getDataManagementFifo()).thenReturn(dataManagementFifo);
        return loApiClient;
    }
}