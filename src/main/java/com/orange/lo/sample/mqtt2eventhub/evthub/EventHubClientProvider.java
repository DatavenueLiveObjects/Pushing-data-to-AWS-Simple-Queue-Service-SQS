/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.mqtt2eventhub.evthub;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.orange.lo.sample.mqtt2eventhub.Counters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

@Configuration
public class EventHubClientProvider {
    private int maxAttempts;
    private Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private EventHubClient ehClient;
    private Counters counters;

    private BlockingQueue<Runnable> tasks;
    private ThreadPoolExecutor tpe;
    private long throttlingDelay;

    @Autowired
    public EventHubClientProvider(EventHubProperties eventHubProperties, Counters counters) throws IOException, EventHubException {
        ConnectionStringBuilder conn = new ConnectionStringBuilder()
                .setOperationTimeout(Duration.of(eventHubProperties.getConnectionTimeout(), ChronoUnit.MILLIS))
                .setNamespaceName(eventHubProperties.getNameSpace())
                .setEventHubName(eventHubProperties.getEvtHubName())
                .setSasKeyName(eventHubProperties.getSasKeyName())
                .setSasKey(eventHubProperties.getSasKey());

        this.counters = counters;

        tasks = new ArrayBlockingQueue<>(eventHubProperties.getTaskQueueSize());
        tpe = new ThreadPoolExecutor(eventHubProperties.getThreadPoolSize(), eventHubProperties.getThreadPoolSize(), 10, TimeUnit.SECONDS, tasks);
        throttlingDelay = eventHubProperties.getThrottlingDelay();
        maxAttempts = eventHubProperties.getMaxSendAttempts();

        log.info("INIT HUB");
        ehClient = EventHubClient.createSync(conn.toString(), Executors.newScheduledThreadPool(10));
    }

    @Bean
    public EvtHubSender evtHubSender() {
        return msg -> {
            try {
                tpe.submit(() -> {
                    counters.evtAttemptCount().increment();
                    send(msg, 0);
                });
            } catch (RejectedExecutionException rejected) {
                counters.evtRejected().increment();
                log.error("too many tasks in queue, rejecting: {}", msg.hashCode());
            }
        };
    }

    private void send(String msg, int attemptCount) {
        byte[] payloadBytes = msg.getBytes(Charset.defaultCharset());
        EventData sendEvent = EventData.create(payloadBytes);
        if (attemptCount > 0) {
            log.info("retrying to send ({}): {}", attemptCount, msg.hashCode());
        }

        try {
            ehClient.sendSync(sendEvent);
            counters.evtSuccess().increment();
        } catch (EventHubException e) {
            counters.evtFailure().increment();
            log.error(e.getMessage());
            if (attemptCount < maxAttempts) {
                counters.evtRetried().increment();
                try {
                    Thread.sleep(throttlingDelay * (1 << attemptCount));
                    send(msg, attemptCount + 1);
                } catch (InterruptedException interrupted) {
                    log.error("interrupted while waiting for resend, aborting");
                    Thread.currentThread().interrupt();
                    counters.evtAborted().increment();
                }
            } else {
                log.error("too many retry attempts, aborting");
                counters.evtAborted().increment();
            }

        } catch (RuntimeException e) {
            log.error(e.toString());
        }
    }

    @PreDestroy
    public void onDestroy() {
        log.info("shutting down executor service");
        tpe.shutdown();
        try {
            ehClient.closeSync();
        } catch (EventHubException e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(fixedRate = 30000)
    public void reportExecutorData() {
        log.info("pool size: {}, active threads: {}, tasks in queue: {}", tpe.getPoolSize(), tpe.getActiveCount(), tpe.getQueue().size());
    }

}
