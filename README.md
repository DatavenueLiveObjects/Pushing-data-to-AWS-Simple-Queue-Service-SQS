# Mqtt2SQS

Spring Boot project reading events from Live Objects MQTT and pushing them to AWS Simple Queue Service (SQS).

## Introduction

This project is intended for Live Objects users wishing to explore integration patterns with AWS and for organizations already running business logic on AWS planning to work on events from IoT devices sourced via Live Objects.

### Requirements

It is assumed that the reader of this document is familiar with Live Objects and AWS. In order to run the connector, it is required to have access to:
- --a Live Objects account
  - with MQTT fifo queue
  - API key which can access the queue (API key generation is described in the [user guide](https://liveobjects.orange-business.com/#/cms/ressources-guide-utilisateur/))
- --an AWS account
  - SQS set up (creation process is described in official [documentation](https://docs.aws.amazon.com/organizations/latest/userguide/orgs_manage_accounts_create.html))
  - Access keys (access key ID and secret access key) to AWS Management Console (see the [documentation](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-setting-up.html))
- --computer with
  - Java-supporting IDE (e.g. IntelliJ, Eclipse)
  - Apache Maven
  - Git client

## Connector

The connector (Mqtt2Sqs) subscribes to selected Live Objects MQTT queue, reads all events and publishes them to selected AWS SQS without any modification to events’ contents. It is intended to be run as a long-running process hosted on AWS. Connector code is written in Java, using the Spring Boot framework.

Mqtt2Sqs supports only the communication from Live Objects i.e. it reads messages send from IoT devices. Communication towards devices (sending commands to devices) is not supported.

Provisioning of IoT devices is within the scope of Live Objects; Mqtt2Sqs has no knowledge on what devices are communicating; it is just transparently moving messages from Live Objects to AWS SQS. It is assumed that business logic acting on those messages is to be applied by applications consuming the messages from SQS.

![architecture](/images/architecture.png)

### Performance & scalability

The software acts as an integration demonstrator. Mqtt2Sqs comes without any guarantees related to percentage of messages successfully written to SQS, nor to the response time. Moreover, the ordering of messages is not guaranteed to be preserved; the application uses thread pools to run its MQTT and Event Hub adapters which may cause some messages to arrive in SQS out of order in which they were kept within Live Objects’ MQTT queue.

Tested on AWS, the connector was processing events at the rate of ~60,000 messages per minute. This should not be treated as a guarantee either, since the throughput depends i.a. on message size and number of applications shared within SQS.

Live Objects platform supports load balancing between multiple MQTT subscribers. It is possible to run multiple instances of Mqtt2SQS, each of them will handle its own subset of messages.

### Installation

In order to be deployed to AWS, the project uses Elastic Beanstalk. The installation description is based on the following tutorial: [documentation](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/GettingStarted.html)

#### App deployment

Deployment to AWS is performed by the Elastic Beanstalk. Its deployment is described in [documentation](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/create_deploy_Java.html).

**JAR deployment**

Build the JAR file using command:

mvn clean package

Deploy the jar file or zip file with the command using AWS console.


#### Application starting

After you create the Elastic Beanstalk application, you can view information about the application you deployed and its provisioned resources by going to the environment dashboard in the AWS Management Console.

The dashboard shows a subset of useful information about your environment. This includes its URL, its current health status, the name of the currently deployed application version, its five most recent events, and the platform version (configuration) on which the application runs configuration.

Application is using standard Spring mechanisms for configuration, with all properties stored within  application.yml file. Properties values can be kept there and/or overridden from other sources (e.g. from environment variables) – see more information in [Spring documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html).

The following properties are used for integration.

### Configuration

##### lo-mqtt.uri
URI of the Live Objects platform.
Default value `ssl://liveobjects.orange-business.com:8883`
##### lo-mqtt.api-key
API key to authorize the MQTT connector. Information on how to create a Live Objects API key can be found [here](https://liveobjects.orange-business.com/doc/html/lo_manual.html#API_KEY).
There is no sensible default value since it should correspond to your account on Live Objects.
##### lo-mqtt.topic
Name of the MQTT queue. For example, if Live Objects web portal displays a FIFO named &quot;dev&quot; (seen under &quot;Data -\&gt; FIFO&quot; option), to subscribe to this queue the property should be set to fifo/dev.
##### aws.sqs.queue-url
URL of your SQS service.

### Metrics
Application sends its metrics to Cloud Watch Metrics every 1 minute. There are number of available metrics e.g. amount of received, sent and failured messages. You can watch them all in Amazon Cloud Watch console under "Metrics" in namespace - mqtt2sqs.

![metric1](/images/metr1.png)

![metric2](/images/metr2.png)

### Health Check
You can configure Alarm to notify when something goes wrong with your environment. Details can be found [here](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/using-features.alarms.html)   

