package com.lute.routes

import com.lute.application.StatsService
import com.lute.dtos.StatsDto
import com.lute.presentation.StatsRoute
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class StatsRoutesTest {
  private val statsService = mockk<StatsService>(relaxed = true)

  private fun testApplicationWithRoutes(block: suspend ApplicationTestBuilder.() -> Unit) =
      testApplication {
        install(ServerContentNegotiation) { json() }
        routing {
          val statsRoute = StatsRoute(statsService)
          statsRoute.register(this)
        }
        block()
      }

  @Test
  fun `GET api-stats returns 200 with stats`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val stats =
        StatsDto(
            total_terms = 100,
            status_distribution = mapOf("Known" to 60, "Unknown" to 40),
        )
    every { statsService.getStats() } returns stats

    val response = client.get("/api/stats")

    assertEquals(HttpStatusCode.OK, response.status)
  }

  @Test
  fun `GET api-stats returns empty stats when no terms`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val stats = StatsDto(total_terms = 0, status_distribution = emptyMap())
    every { statsService.getStats() } returns stats

    val response = client.get("/api/stats")

    assertEquals(HttpStatusCode.OK, response.status)
  }
}
