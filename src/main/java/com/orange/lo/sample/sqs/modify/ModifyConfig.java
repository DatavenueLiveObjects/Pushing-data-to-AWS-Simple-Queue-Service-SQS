/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.modify;

import java.io.File;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;

@Configuration
public class ModifyConfig {

	private static final String CONFIGURATION_FILE_NAME = "application.yml";
	private static final String AWS_CONFIGURATION_FILE_PATH = "/root/.aws/credentials";

	@Bean
	public File configurationFile() throws IOException {
		return new ClassPathResource(CONFIGURATION_FILE_NAME).getFile();
	}

	@Bean
	public File awsConfigurationFile() throws IOException {
		return new File(AWS_CONFIGURATION_FILE_PATH);
	}

	@Bean
	public ProfileCredentialsProvider profileCredentialsProvider() {
		return new ProfileCredentialsProvider();
	}
}