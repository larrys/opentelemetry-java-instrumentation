plugins {
  id("otel.java-conventions")
}

dependencies {
  api(project(":testing-common"))

  api("com.squareup.okhttp3:okhttp:3.0.0")

  implementation("io.opentelemetry:opentelemetry-api")
}
