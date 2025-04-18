/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jaxrs.v3_0;

import io.opentelemetry.instrumentation.jaxrs.v3_0.JaxRsJettyHttpServerTest;
import io.opentelemetry.instrumentation.testing.junit.InstrumentationExtension;
import io.opentelemetry.instrumentation.testing.junit.http.HttpServerInstrumentationExtension;
import io.opentelemetry.instrumentation.testing.junit.http.HttpServerTestOptions;
import org.junit.jupiter.api.extension.RegisterExtension;

class JerseyJettyHttpServerTest extends JaxRsJettyHttpServerTest {
  @RegisterExtension
  static final InstrumentationExtension testing = HttpServerInstrumentationExtension.forAgent();

  @Override
  protected void configure(HttpServerTestOptions options) {
    super.configure(options);

    options.setResponseCodeOnNonStandardHttpMethod(500);
  }

  @Override
  protected boolean asyncCancelHasSendError() {
    return true;
  }

  @Override
  protected boolean testInterfaceMethodWithPath() {
    // disables a test that jersey deems invalid
    return false;
  }
}
