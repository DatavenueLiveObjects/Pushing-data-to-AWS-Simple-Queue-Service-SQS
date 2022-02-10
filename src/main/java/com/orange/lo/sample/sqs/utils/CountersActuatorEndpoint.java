package com.orange.lo.sample.sqs.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;

@Component
@Endpoint(id = "counters")
public class CountersActuatorEndpoint {

  private final Counters counters;

  public CountersActuatorEndpoint(Counters counters) {
    this.counters = counters;
  }

  @ReadOperation
  public Map<String, Long> getCountersStatus() {
    HashMap<String, Long> countersStatus = new HashMap<>();
    counters.getAll().forEach(c -> countersStatus.put(c.getId().getName(), val(c)));
    return countersStatus;
  }
  
  private long val(Counter cnt) {
      return Math.round(cnt.count());
  }
}