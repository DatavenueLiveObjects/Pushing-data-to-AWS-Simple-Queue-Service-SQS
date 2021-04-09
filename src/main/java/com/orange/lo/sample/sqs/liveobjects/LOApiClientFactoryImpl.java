/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

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
