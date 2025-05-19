/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sqs;

import com.orange.lo.sample.sqs.liveobjects.LoMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Configuration
public class ApplicationConfig {

    @Bean
    public Queue<LoMessage> messageQueue() {
        return new ConcurrentLinkedQueue<>();
    }

}