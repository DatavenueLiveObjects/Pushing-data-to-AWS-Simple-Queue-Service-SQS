/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs.modify;

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.orange.lo.sample.sqs.ConnectorApplication;
import com.orange.lo.sample.sqs.liveobjects.LoProperties;
import com.orange.lo.sample.sqs.sqs.SqsProperties;

@ExtendWith(MockitoExtension.class)
public class ModifyConfigurationServiceTest {

	private static final String LO_API_KEY = "abcd";
	private static final String LO_API_KEY_UPDATED = "dcba";
	
	private static final long CONNECTION_TIMEOUT = 5000;
	private static final long CONNECTION_TIMEOUT_UPDATED = 6000;
	
	private static final String AWS_ACCESS_KEY = "aaaaa";
	private static final String AWS_ACCESS_KEY_UPDATED = "bbbbb";
	
	@TempDir
	File tempDir;
	
	@Mock
	private ProfileCredentialsProvider profileCredentialsProvider;

	private File configurationFile;
	private File awsConfigurationFile;

	private ModifyConfigurationService modifyConfigurationService;
	
	@BeforeEach
    void setUp() throws IOException {
		LoProperties loProperties = new LoProperties();
		loProperties.setApiKey(LO_API_KEY);
		
		SqsProperties sqsProperties = new SqsProperties();
		sqsProperties.setConnectionTimeout(CONNECTION_TIMEOUT);
		
		configurationFile = new File(tempDir, "application.yml");
		FileUtils.fileWrite(configurationFile, "lo:\n  api-key: " + LO_API_KEY + "\naws:\n  sqs:\n    connection-timeout: " + CONNECTION_TIMEOUT);
		
		awsConfigurationFile = new File(tempDir, "credentials");
		FileUtils.fileWrite(awsConfigurationFile, "[default]\naws_access_key_id = " + AWS_ACCESS_KEY + "\naws_secret_access_key = BBB");
		
		modifyConfigurationService = new ModifyConfigurationService(loProperties, sqsProperties, profileCredentialsProvider, configurationFile, awsConfigurationFile);
	}
	
	@Test
	public void shouldReadParameters() {
		//given
		when(profileCredentialsProvider.getCredentials()).thenReturn(getAWSCredentials());
		
		//when
		ModifyConfigurationProperties properties = modifyConfigurationService.getProperties();
		
		//then
		Assertions.assertEquals(LO_API_KEY, properties.getLoApiKey());
		Assertions.assertEquals(CONNECTION_TIMEOUT, properties.getSqsConnectionTimeout());
		Assertions.assertEquals(AWS_ACCESS_KEY, properties.getAwsAccessKey());
	}
	
	@Test
	public void shouldUpdateParameters() throws IOException {
		//given
		MockedStatic<ConnectorApplication> connectorApplication = Mockito.mockStatic(ConnectorApplication.class);
		ModifyConfigurationProperties modifyConfigurationProperties = new ModifyConfigurationProperties();
		modifyConfigurationProperties.setSqsConnectionTimeout(CONNECTION_TIMEOUT_UPDATED);
		modifyConfigurationProperties.setLoApiKey(LO_API_KEY_UPDATED);
		modifyConfigurationProperties.setAwsAccessKey(AWS_ACCESS_KEY_UPDATED);
		
		//when
		modifyConfigurationService.modify(modifyConfigurationProperties);
		
		//then
		String configuratioFileContent = FileUtils.fileRead(configurationFile);
		String awsFileContent = FileUtils.fileRead(awsConfigurationFile);
		
		connectorApplication.verify(ConnectorApplication::restart);
		Assertions.assertTrue(configuratioFileContent.contains(LO_API_KEY_UPDATED));
		Assertions.assertTrue(configuratioFileContent.contains(String.valueOf(CONNECTION_TIMEOUT_UPDATED)));
		Assertions.assertTrue(awsFileContent.contains(AWS_ACCESS_KEY_UPDATED));
	}
	
	private AWSCredentials getAWSCredentials() {
		return new AWSCredentials() {
			
			@Override
			public String getAWSSecretKey() {
				return "BBB";
			}
			
			@Override
			public String getAWSAccessKeyId() {

				return AWS_ACCESS_KEY;
			}
		};
	}
}