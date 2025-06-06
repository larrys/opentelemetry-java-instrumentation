/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.servlet.v3_0.jetty;

import static io.opentelemetry.instrumentation.testing.junit.http.ServerEndpoint.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.javaagent.instrumentation.servlet.v3_0.TestServlet3;
import io.opentelemetry.testing.internal.armeria.common.AggregatedHttpRequest;
import io.opentelemetry.testing.internal.armeria.common.AggregatedHttpResponse;
import io.opentelemetry.testing.internal.armeria.common.HttpMethod;
import javax.servlet.Servlet;
import org.junit.jupiter.api.Test;

class JettyServlet3AsyncTest extends JettyServlet3Test {

  @Override
  public Class<? extends Servlet> servlet() {
    return TestServlet3.Async.class;
  }

  @Override
  public boolean errorEndpointUsesSendError() {
    return false;
  }

  @Override
  public boolean isAsyncTest() {
    return true;
  }

  @Test
  void startAsyncInSpan() {
    AggregatedHttpRequest request =
        AggregatedHttpRequest.of(
            HttpMethod.GET, resolveAddress(SUCCESS, "h1c://") + "?startAsyncInSpan=true");
    AggregatedHttpResponse response = client.execute(request).aggregate().join();

    assertThat(response.status().code()).isEqualTo(SUCCESS.getStatus());
    assertThat(response.contentUtf8()).isEqualTo(SUCCESS.getBody());

    testing()
        .waitAndAssertTraces(
            trace ->
                trace.hasSpansSatisfyingExactly(
                    span ->
                        span.hasName("GET " + getContextPath() + "/success")
                            .hasKind(SpanKind.SERVER)
                            .hasNoParent(),
                    span ->
                        span.hasName("startAsync")
                            .hasKind(SpanKind.INTERNAL)
                            .hasParent(trace.getSpan(0)),
                    span ->
                        span.hasName("controller")
                            .hasKind(SpanKind.INTERNAL)
                            .hasParent(trace.getSpan(0))));
  }
}
