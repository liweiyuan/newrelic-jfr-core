/*
 *
 *  * Copyright 2020 New Relic Corporation. All rights reserved.
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.newrelic.jfr.toevent;

import static com.newrelic.jfr.RecordedObjectValidators.*;

import com.newrelic.jfr.MethodSupport;
import com.newrelic.jfr.Workarounds;
import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.events.Event;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;

// jdk.Compilation {
//        startTime = 16:04:14.403
//        duration = LGTM - but I'm not a reviewer :( ms
//        method = org.apache.kafka.clients.Metadata.update(Cluster, Set, long)
//        compileId = 30333
//        compileLevel = 4
//        succeded = true
//        isOsr = false
//        codeSize = 36.1 kB
//        inlinedBytes = 2.9 kB
//        eventThread = "C2 CompilerThread0" (javaThreadId = 5)
//        }
public class JITCompilationMapper implements EventToEvent {
  private static final String SIMPLE_CLASS_NAME = JITCompilationMapper.class.getSimpleName();
  public static final String EVENT_NAME = "jdk.Compilation";
  private static final String METHOD = "method";
  private static final String DESC = "desc";
  private static final String DURATION = "duration";
  private static final String SUCCEEDED = "succeeded";
  private static final String EVENT_THREAD = "eventThread";
  private static final String THREAD_NAME = "thread.name";

  @Override
  public List<Event> apply(RecordedEvent event) {
    long timestamp = event.getStartTime().toEpochMilli();
    Duration duration = event.getDuration();
    Attributes attr = new Attributes();
    if (hasField(event, METHOD, SIMPLE_CLASS_NAME)) {
      attr.put(DESC, MethodSupport.describeMethod(event.getValue(METHOD)));
    }
    attr.put(DURATION, duration.toMillis());
    attr.put(SUCCEEDED, Workarounds.getSucceeded(event));
    RecordedThread threadId = null;
    if (hasField(event, EVENT_THREAD, SIMPLE_CLASS_NAME)) {
      threadId = event.getThread(EVENT_THREAD);
    }
    attr.put(THREAD_NAME, threadId == null ? null : threadId.getJavaName());
    return Collections.singletonList(new Event("JfrCompilation", attr, timestamp));
  }

  @Override
  public String getEventName() {
    return EVENT_NAME;
  }
}
