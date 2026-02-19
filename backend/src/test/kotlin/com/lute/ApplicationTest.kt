package com.lute

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
  @Test
  fun `health endpoint returns ok`() = testApplication {
    application { module() }
    val response = client.get("/api/health")
    assertEquals(HttpStatusCode.OK, response.status)
    assert(response.bodyAsText().contains("\"status\""))
    assert(response.bodyAsText().contains("\"ok\""))
  }
}
