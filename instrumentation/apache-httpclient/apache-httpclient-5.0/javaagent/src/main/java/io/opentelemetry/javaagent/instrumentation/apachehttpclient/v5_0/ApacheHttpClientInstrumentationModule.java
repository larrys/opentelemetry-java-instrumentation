/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.apachehttpclient.v5_0;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.bootstrap.internal.AgentInstrumentationConfig;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import java.util.Arrays;
import java.util.List;

@AutoService(InstrumentationModule.class)
public class ApacheHttpClientInstrumentationModule extends InstrumentationModule {

  public ApacheHttpClientInstrumentationModule() {
    super("apache-httpclient", "apache-httpclient-5.0");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    boolean debug =
        AgentInstrumentationConfig.get()
            .getBoolean("otel.instrumentation.apache-httpclient-5.debug", false);
    if (debug) {
      return Arrays.asList(
          new ApacheHttpClientInstrumentation(),
          new ApacheHttpAsyncClientInstrumentation(),
          new IoReactorDebugInstrumentation());
    }
    return Arrays.asList(
        new ApacheHttpClientInstrumentation(), new ApacheHttpAsyncClientInstrumentation());
  }
}
