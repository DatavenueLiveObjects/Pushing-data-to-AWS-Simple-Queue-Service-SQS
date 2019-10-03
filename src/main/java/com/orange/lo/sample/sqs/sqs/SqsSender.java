/** 
* Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved. 
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.sqs.sqs;

import java.util.List;

public interface SqsSender {

    void send(List<String> message);

}
