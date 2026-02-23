package com.lute.routes

import com.lute.application.TermService
import com.lute.application.exceptions.DuplicateEntityException
import com.lute.application.exceptions.EntityNotFoundException
import com.lute.domain.ErrorResponse
import com.lute.dtos.BulkOperationDto
import com.lute.dtos.BulkOperationResult
import com.lute.dtos.CreateTermDto
import com.lute.dtos.ImportResult
import com.lute.dtos.TermDto
import com.lute.dtos.UpdateTermDto
import com.lute.presentation.TermRoutes
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

class TermRoutesTest {
  private val termService = mockk<TermService>(relaxed = true)

  private fun testApplicationWithRoutes(block: suspend ApplicationTestBuilder.() -> Unit) =
      testApplication {
        install(ServerContentNegotiation) { json() }
        install(StatusPages) {
          exception<EntityNotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(cause.message ?: "Term not found"),
            )
          }
          exception<DuplicateEntityException> { call, cause ->
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse(cause.message ?: "Term already exists"),
            )
          }
        }
        routing {
          val termRoutes = TermRoutes(termService)
          termRoutes.register(this)
        }
        block()
      }

  @Test
  fun `GET api-terms returns 200 with terms`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val terms =
        listOf(
            TermDto(id = 1, text = "hello", language_id = 1L, status = 0),
            TermDto(id = 2, text = "world", language_id = 1L, status = 1),
        )
    every { termService.getAllTerms(null, null, 100, 0) } returns terms

    val response = client.get("/api/terms")

    assertEquals(HttpStatusCode.OK, response.status)
  }

  @Test
  fun `GET api-terms with filters passes parameters`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val terms = listOf(TermDto(id = 1, text = "hello", language_id = 1L, status = 1))
    every { termService.getAllTerms(1L, 1, 50, 10) } returns terms

    val response = client.get("/api/terms?language_id=1&status=1&limit=50&offset=10")

    assertEquals(HttpStatusCode.OK, response.status)
    verify { termService.getAllTerms(1L, 1, 50, 10) }
  }

  @Test
  fun `GET api-terms with search uses searchTerms`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val terms = listOf(TermDto(id = 1, text = "hello", language_id = 1L, status = 0))
    every { termService.searchTerms("hel", null, null) } returns terms

    val response = client.get("/api/terms?search=hel")

    assertEquals(HttpStatusCode.OK, response.status)
    verify { termService.searchTerms("hel", null, null) }
  }

  @Test
  fun `GET api-terms-id returns 200 for existing term`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val term = TermDto(id = 1, text = "hello", language_id = 1L, status = 0)
    every { termService.getTermById(1L) } returns term

    val response = client.get("/api/terms/1")

    assertEquals(HttpStatusCode.OK, response.status)
  }

  @Test
  fun `GET api-terms-id returns 404 for non-existent term`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    every { termService.getTermById(999L) } returns null

    val response = client.get("/api/terms/999")

    assertEquals(HttpStatusCode.NotFound, response.status)
  }

  @Test
  fun `GET api-terms-id returns 400 for invalid ID`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }

    val response = client.get("/api/terms/invalid")

    assertEquals(HttpStatusCode.BadRequest, response.status)
  }

  @Test
  fun `POST api-terms returns 201 for valid data`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val dto = CreateTermDto(text = "hello", language_id = 1L)
    val created = TermDto(id = 1, text = "hello", language_id = 1L, status = 0)
    every { termService.createTerm(dto) } returns created

    val response =
        client.post("/api/terms") {
          contentType(ContentType.Application.Json)
          setBody(dto)
        }

    assertEquals(HttpStatusCode.Created, response.status)
  }

  @Test
  fun `POST api-terms returns 409 for duplicate term`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val dto = CreateTermDto(text = "hello", language_id = 1L)
    every { termService.createTerm(dto) } throws DuplicateEntityException("Term", "hello")

    val response =
        client.post("/api/terms") {
          contentType(ContentType.Application.Json)
          setBody(dto)
        }

    assertEquals(HttpStatusCode.Conflict, response.status)
  }

  @Test
  fun `PATCH api-terms-id returns 200 for valid update`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val dto = UpdateTermDto(status = 3)
    val updated = TermDto(id = 1, text = "hello", language_id = 1L, status = 3)
    every { termService.updateTerm(1L, dto) } returns updated

    val response =
        client.patch("/api/terms/1") {
          contentType(ContentType.Application.Json)
          setBody(dto)
        }

    assertEquals(HttpStatusCode.OK, response.status)
  }

  @Test
  fun `PATCH api-terms-id returns 404 for non-existent term`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val dto = UpdateTermDto(status = 3)
    every { termService.updateTerm(999L, dto) } returns null

    val response =
        client.patch("/api/terms/999") {
          contentType(ContentType.Application.Json)
          setBody(dto)
        }

    assertEquals(HttpStatusCode.NotFound, response.status)
  }

  @Test
  fun `PATCH api-terms-id returns 400 for invalid ID`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val dto = UpdateTermDto(status = 3)

    val response =
        client.patch("/api/terms/invalid") {
          contentType(ContentType.Application.Json)
          setBody(dto)
        }

    assertEquals(HttpStatusCode.BadRequest, response.status)
  }

  @Test
  fun `DELETE api-terms-id returns 204 for successful delete`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    every { termService.deleteTerm(1L) } returns true

    val response = client.delete("/api/terms/1")

    assertEquals(HttpStatusCode.NoContent, response.status)
    verify { termService.deleteTerm(1L) }
  }

  @Test
  fun `DELETE api-terms-id returns 404 for non-existent term`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    every { termService.deleteTerm(999L) } returns false

    val response = client.delete("/api/terms/999")

    assertEquals(HttpStatusCode.NotFound, response.status)
  }

  @Test
  fun `POST api-terms-bulk returns 200 for update_status`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val dto = BulkOperationDto(operation = "update_status", term_ids = listOf(1L, 2L), status = 3)
    val result = BulkOperationResult(updated = 2, failed = 0)
    every { termService.bulkOperation("update_status", listOf(1L, 2L), 3, null) } returns result

    val response =
        client.post("/api/terms/bulk") {
          contentType(ContentType.Application.Json)
          setBody(dto)
        }

    assertEquals(HttpStatusCode.OK, response.status)
  }

  @Test
  fun `POST api-terms-bulk returns 200 for add_tags`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val dto = BulkOperationDto(operation = "add_tags", term_ids = listOf(1L), tag_ids = listOf(10L))
    val result = BulkOperationResult(updated = 1, failed = 0)
    every { termService.bulkOperation("add_tags", listOf(1L), null, listOf(10L)) } returns result

    val response =
        client.post("/api/terms/bulk") {
          contentType(ContentType.Application.Json)
          setBody(dto)
        }

    assertEquals(HttpStatusCode.OK, response.status)
  }

  @Test
  fun `GET api-terms-export returns CSV`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val csvBytes = "text,translation\nhello,hola\n".toByteArray()
    every { termService.exportToCsv(null, null) } returns csvBytes

    val response = client.get("/api/terms/export")

    assertEquals(HttpStatusCode.OK, response.status)
    assertEquals(ContentType.Text.CSV, response.contentType())
  }

  @Test
  fun `GET api-terms-export with filters passes parameters`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val csvBytes = "text,translation\n".toByteArray()
    every { termService.exportToCsv(1L, 5) } returns csvBytes

    val response = client.get("/api/terms/export?language_id=1&status=5")

    assertEquals(HttpStatusCode.OK, response.status)
    verify { termService.exportToCsv(1L, 5) }
  }

  @Test
  fun `POST api-terms-import returns 200 for successful import`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val csv = "text,translation\nhello,hola"
    val result = ImportResult(imported = 1, skipped = 0)
    every { termService.importFromCsv(csv, 1L) } returns result

    val response =
        client.post("/api/terms/import?language_id=1") {
          contentType(ContentType.Text.Plain)
          setBody(csv)
        }

    assertEquals(HttpStatusCode.OK, response.status)
  }

  @Test
  fun `POST api-terms-import returns 400 when language_id missing`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val csv = "text,translation\nhello,hola"

    val response =
        client.post("/api/terms/import") {
          contentType(ContentType.Text.Plain)
          setBody(csv)
        }

    assertEquals(HttpStatusCode.BadRequest, response.status)
  }

  @Test
  fun `GET api-terms-id-parents returns 200 with parents`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val parents = listOf(TermDto(id = 2, text = "parent", language_id = 1L, status = 5))
    every { termService.getParents(1L) } returns parents

    val response = client.get("/api/terms/1/parents")

    assertEquals(HttpStatusCode.OK, response.status)
  }

  @Test
  fun `GET api-terms-id-parents returns 404 for non-existent term`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    every { termService.getParents(999L) } throws EntityNotFoundException("Term", 999L)

    val response = client.get("/api/terms/999/parents")

    assertEquals(HttpStatusCode.NotFound, response.status)
  }

  @Test
  fun `POST api-terms-id-parents returns 201 for valid parent`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    every { termService.addParent(1L, 2L) } returns Unit

    val response = client.post("/api/terms/1/parents?parent_id=2")

    assertEquals(HttpStatusCode.Created, response.status)
    verify { termService.addParent(1L, 2L) }
  }

  @Test
  fun `POST api-terms-id-parents returns 400 when parent_id missing`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }

    val response = client.post("/api/terms/1/parents")

    assertEquals(HttpStatusCode.BadRequest, response.status)
  }

  @Test
  fun `DELETE api-terms-id-parents-parentId returns 204`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    every { termService.removeParent(1L, 2L) } returns Unit

    val response = client.delete("/api/terms/1/parents/2")

    assertEquals(HttpStatusCode.NoContent, response.status)
    verify { termService.removeParent(1L, 2L) }
  }
}
