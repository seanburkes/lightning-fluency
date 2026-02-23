package com.lute.routes

import com.lute.application.ReadingService
import com.lute.application.exceptions.EntityNotFoundException
import com.lute.domain.ErrorResponse
import com.lute.dtos.PageDto
import com.lute.dtos.ReadingPageDto
import com.lute.dtos.SaveCurrentPageDto
import com.lute.dtos.TokenDto
import com.lute.presentation.ReadingRoutes
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadingRoutesTest {
  private val readingService = mockk<ReadingService>(relaxed = true)

  private fun testApplicationWithRoutes(block: suspend ApplicationTestBuilder.() -> Unit) =
      testApplication {
        install(ServerContentNegotiation) { json() }
        install(StatusPages) {
          exception<EntityNotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(cause.message ?: "Entity not found"),
            )
          }
        }
        routing {
          val readingRoutes = ReadingRoutes(readingService)
          readingRoutes.register(this)
        }
        block()
      }

  @Test
  fun `GET api-read-bookId-pages-pageNum returns 200 for existing page`() =
      testApplicationWithRoutes {
        val client = createClient { install(ClientContentNegotiation) { json() } }
        val pageDto =
            ReadingPageDto(
                page = PageDto(id = 100L, order = 1, text = "Hello world"),
                tokens =
                    listOf(
                        TokenDto(token = "Hello", is_word = true, status = null),
                        TokenDto(token = " ", is_word = false, status = null),
                        TokenDto(token = "world", is_word = true, status = null),
                    ),
            )
        every { readingService.getPage(1L, 1) } returns pageDto

        val response = client.get("/api/read/1/pages/1")

        assertEquals(HttpStatusCode.OK, response.status)
      }

  @Test
  fun `GET api-read-bookId-pages-pageNum returns 404 for non-existent page`() =
      testApplicationWithRoutes {
        val client = createClient { install(ClientContentNegotiation) { json() } }
        every { readingService.getPage(1L, 999) } returns null

        val response = client.get("/api/read/1/pages/999")

        assertEquals(HttpStatusCode.NotFound, response.status)
      }

  @Test
  fun `GET api-read-bookId-pages-pageNum returns 400 for invalid bookId`() =
      testApplicationWithRoutes {
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/api/read/invalid/pages/1")

        assertEquals(HttpStatusCode.BadRequest, response.status)
      }

  @Test
  fun `GET api-read-bookId-pages-pageNum returns 400 for invalid pageNum`() =
      testApplicationWithRoutes {
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/api/read/1/pages/invalid")

        assertEquals(HttpStatusCode.BadRequest, response.status)
      }

  @Test
  fun `GET api-read-bookId-pages-next returns next page number`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    every { readingService.getNextPage(1L, 1) } returns 2

    val response = client.get("/api/read/1/pages/next?current=1")

    assertEquals(HttpStatusCode.OK, response.status)
  }

  @Test
  fun `GET api-read-bookId-pages-next returns 204 when at end`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    every { readingService.getNextPage(1L, 1) } returns null

    val response = client.get("/api/read/1/pages/next?current=1")

    assertEquals(HttpStatusCode.NoContent, response.status)
  }

  @Test
  fun `GET api-read-bookId-pages-next defaults current to 1 when not provided`() =
      testApplicationWithRoutes {
        val client = createClient { install(ClientContentNegotiation) { json() } }
        every { readingService.getNextPage(1L, 1) } returns 2

        val response = client.get("/api/read/1/pages/next")

        assertEquals(HttpStatusCode.OK, response.status)
        verify { readingService.getNextPage(1L, 1) }
      }

  @Test
  fun `GET api-read-bookId-pages-prev returns previous page number`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    every { readingService.getPreviousPage(1L, 2) } returns 1

    val response = client.get("/api/read/1/pages/prev?current=2")

    assertEquals(HttpStatusCode.OK, response.status)
  }

  @Test
  fun `GET api-read-bookId-pages-prev returns 204 when at beginning`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    every { readingService.getPreviousPage(1L, 1) } returns null

    val response = client.get("/api/read/1/pages/prev?current=1")

    assertEquals(HttpStatusCode.NoContent, response.status)
  }

  @Test
  fun `GET api-read-bookId-current returns current page number`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    every { readingService.getCurrentPage(1L) } returns 5

    val response = client.get("/api/read/1/current")

    assertEquals(HttpStatusCode.OK, response.status)
  }

  @Test
  fun `GET api-read-bookId-current returns 204 when not started`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    every { readingService.getCurrentPage(1L) } returns null

    val response = client.get("/api/read/1/current")

    assertEquals(HttpStatusCode.NoContent, response.status)
  }

  @Test
  fun `POST api-read-bookId-current saves current page`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val dto = SaveCurrentPageDto(page_num = 5)
    every { readingService.saveCurrentPage(1L, 5) } returns Unit

    val response =
        client.post("/api/read/1/current") {
          contentType(ContentType.Application.Json)
          setBody(dto)
        }

    assertEquals(HttpStatusCode.OK, response.status)
    verify { readingService.saveCurrentPage(1L, 5) }
  }

  @Test
  fun `POST api-read-bookId-current returns 400 for invalid bookId`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val dto = SaveCurrentPageDto(page_num = 5)

    val response =
        client.post("/api/read/invalid/current") {
          contentType(ContentType.Application.Json)
          setBody(dto)
        }

    assertEquals(HttpStatusCode.BadRequest, response.status)
  }

  @Test
  fun `POST api-read-bookId-current returns 404 for non-existent book`() =
      testApplicationWithRoutes {
        val client = createClient { install(ClientContentNegotiation) { json() } }
        val dto = SaveCurrentPageDto(page_num = 5)
        every { readingService.saveCurrentPage(999L, 5) } throws
            EntityNotFoundException("Book", 999L)

        val response =
            client.post("/api/read/999/current") {
              contentType(ContentType.Application.Json)
              setBody(dto)
            }

        assertEquals(HttpStatusCode.NotFound, response.status)
      }
}
