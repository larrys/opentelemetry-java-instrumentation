/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.cassandra.v4_0;

import com.datastax.oss.driver.api.core.cql.ExecutionInfo;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.incubator.semconv.db.DbClientMetrics;
import io.opentelemetry.instrumentation.api.incubator.semconv.db.DbClientSpanNameExtractor;
import io.opentelemetry.instrumentation.api.incubator.semconv.db.SqlClientAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;
import io.opentelemetry.instrumentation.api.semconv.network.NetworkAttributesExtractor;
import io.opentelemetry.javaagent.bootstrap.internal.AgentCommonConfig;
import io.opentelemetry.semconv.incubating.DbIncubatingAttributes;

@SuppressWarnings("deprecation") // using deprecated semconv
public final class CassandraSingletons {
  private static final String INSTRUMENTATION_NAME = "io.opentelemetry.cassandra-4.0";

  // using ExecutionInfo because we can get that from ResultSet, AsyncResultSet and DriverException
  private static final Instrumenter<CassandraRequest, ExecutionInfo> INSTRUMENTER;

  static {
    CassandraSqlAttributesGetter attributesGetter = new CassandraSqlAttributesGetter();

    INSTRUMENTER =
        Instrumenter.<CassandraRequest, ExecutionInfo>builder(
                GlobalOpenTelemetry.get(),
                INSTRUMENTATION_NAME,
                DbClientSpanNameExtractor.create(attributesGetter))
            .addAttributesExtractor(
                SqlClientAttributesExtractor.builder(attributesGetter)
                    .setTableAttribute(DbIncubatingAttributes.DB_CASSANDRA_TABLE)
                    .setStatementSanitizationEnabled(
                        AgentCommonConfig.get().isStatementSanitizationEnabled())
                    .build())
            .addAttributesExtractor(
                NetworkAttributesExtractor.create(new CassandraNetworkAttributesGetter()))
            .addAttributesExtractor(new CassandraAttributesExtractor())
            .addOperationMetrics(DbClientMetrics.get())
            .buildInstrumenter(SpanKindExtractor.alwaysClient());
  }

  public static Instrumenter<CassandraRequest, ExecutionInfo> instrumenter() {
    return INSTRUMENTER;
  }

  private CassandraSingletons() {}
}
