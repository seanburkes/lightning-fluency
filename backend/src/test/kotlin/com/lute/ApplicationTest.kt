package com.lute

import com.lute.db.DatabaseFactory
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.AfterEach

class ApplicationTest {
  @AfterEach
  fun tearDown() {
    DatabaseFactory.shutdown()
  }

  @Test
  fun `health endpoint returns ok`() = testApplication {
    application {
      DatabaseFactory.init(":memory:")
      module()
    }
    val response = client.get("/api/health")
    assertEquals(HttpStatusCode.OK, response.status)
    assert(response.bodyAsText().contains("\"status\""))
    assert(response.bodyAsText().contains("\"ok\""))
  }
}
