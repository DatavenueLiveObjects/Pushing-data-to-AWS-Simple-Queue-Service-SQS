/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs;

import com.orange.lo.sample.sqs.liveobjects.LoMessage;
import com.orange.lo.sample.sqs.sqs.SqsProperties;
import com.orange.lo.sample.sqs.utils.MetricsProperties;
import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.providers.AwsProfileRegionProvider;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Configuration
public class ApplicationConfig {

	private final MetricsProperties metricsProperties;
	private final SqsProperties sqsProperties;

	ApplicationConfig(MetricsProperties metricsProperties, SqsProperties sqsProperties) {
		this.metricsProperties = metricsProperties;
        this.sqsProperties = sqsProperties;
    }

    @Bean
    public Queue<LoMessage> messageQueue() {
        return new ConcurrentLinkedQueue<>();
    }

	@Bean
	public MeterRegistry meterRegistry() {

		CloudWatchAsyncClient cloudWatchAsyncClient = CloudWatchAsyncClient.builder()
				.credentialsProvider(ProfileCredentialsProvider.create(sqsProperties.getServiceProfileName()))
				.region(new AwsProfileRegionProvider(null, sqsProperties.getServiceProfileName()).getRegion())
				.build();

		CloudWatchMeterRegistry cloudWatchMeterRegistry = new CloudWatchMeterRegistry(cloudWatchConfig(), Clock.SYSTEM, cloudWatchAsyncClient);

    	cloudWatchMeterRegistry.config()
				.meterFilter(MeterFilter.deny(id -> !id.getName().startsWith("message")))
				.commonTags(metricsProperties.getDimensionName(), metricsProperties.getDimensionValue());
		return cloudWatchMeterRegistry;
	}

	private CloudWatchConfig cloudWatchConfig() {
		return new CloudWatchConfig() {

			@Override
			public String get(String key) {
				return null;
			}

			@Override
			public String namespace() {
				return metricsProperties.getNamespace();
			}
		};
	}
}