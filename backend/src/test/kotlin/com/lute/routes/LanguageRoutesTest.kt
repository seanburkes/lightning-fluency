package com.lute.routes

import com.lute.application.DictionaryService
import com.lute.application.LanguageService
import com.lute.application.exceptions.DuplicateEntityException
import com.lute.application.exceptions.EntityInUseException
import com.lute.application.exceptions.EntityNotFoundException
import com.lute.application.exceptions.ValidationException
import com.lute.domain.ErrorResponse
import com.lute.dtos.*
import com.lute.presentation.LanguageRoutes
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

class LanguageRoutesTest {
  private val languageService = mockk<LanguageService>(relaxed = true)
  private val dictionaryService = mockk<DictionaryService>(relaxed = true)

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
          exception<EntityInUseException> { call, cause ->
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse(cause.message ?: "Entity is in use"),
            )
          }
          exception<DuplicateEntityException> { call, cause ->
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse(cause.message ?: "Entity already exists"),
            )
          }
          exception<ValidationException> { call, cause ->
            val errors = cause.errors.map { (field, message) -> ValidationError(field, message) }
            call.respond(HttpStatusCode.BadRequest, ValidationErrorResponse(errors))
          }
        }
        routing {
          val languageRoutes = LanguageRoutes(languageService, dictionaryService)
          languageRoutes.register(this)
        }
        block()
      }

  @Test
  fun `GET api-languages returns 200 with languages`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val languages =
        listOf(
            LanguageDto(id = 1, name = "English", parser_type = "spacedel"),
            LanguageDto(id = 2, name = "Spanish", parser_type = "spacedel"),
        )
    every { languageService.getAllLanguages() } returns languages

    val response = client.get("/api/languages")

    assertEquals(HttpStatusCode.OK, response.status)
  }

  @Test
  fun `GET api-languages-id returns 200 for existing language`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val language = LanguageDto(id = 1, name = "English", parser_type = "spacedel")
    every { languageService.getLanguageById(1) } returns language

    val response = client.get("/api/languages/1")

    assertEquals(HttpStatusCode.OK, response.status)
  }

  @Test
  fun `GET api-languages-id returns 404 for non-existent language`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    every { languageService.getLanguageById(999) } returns null

    val response = client.get("/api/languages/999")

    assertEquals(HttpStatusCode.NotFound, response.status)
  }

  @Test
  fun `GET api-languages-id returns 400 for invalid ID`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }

    val response = client.get("/api/languages/invalid")

    assertEquals(HttpStatusCode.BadRequest, response.status)
  }

  @Test
  fun `POST api-languages returns 201 for valid data`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val dto = CreateLanguageDto(name = "French", parser_type = "spacedel")
    val created = LanguageDto(id = 1, name = "French", parser_type = "spacedel")
    every { languageService.createLanguage(dto) } returns created

    val response =
        client.post("/api/languages") {
          contentType(ContentType.Application.Json)
          setBody(dto)
        }

    assertEquals(HttpStatusCode.Created, response.status)
  }

  @Test
  fun `POST api-languages returns 409 for duplicate name`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val dto = CreateLanguageDto(name = "English", parser_type = "spacedel")
    every { languageService.createLanguage(dto) } throws
        DuplicateEntityException("Language", "English")

    val response =
        client.post("/api/languages") {
          contentType(ContentType.Application.Json)
          setBody(dto)
        }

    assertEquals(HttpStatusCode.Conflict, response.status)
  }

  @Test
  fun `PATCH api-languages-id returns 200 for valid update`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val dto = UpdateLanguageDto(name = "British English")
    val updated = LanguageDto(id = 1, name = "British English", parser_type = "spacedel")
    every { languageService.updateLanguage(1, dto) } returns updated

    val response =
        client.patch("/api/languages/1") {
          contentType(ContentType.Application.Json)
          setBody(dto)
        }

    assertEquals(HttpStatusCode.OK, response.status)
  }

  @Test
  fun `PATCH api-languages-id returns 404 for non-existent language`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val dto = UpdateLanguageDto(name = "Test")
    every { languageService.updateLanguage(999, dto) } returns null

    val response =
        client.patch("/api/languages/999") {
          contentType(ContentType.Application.Json)
          setBody(dto)
        }

    assertEquals(HttpStatusCode.NotFound, response.status)
  }

  @Test
  fun `DELETE api-languages-id returns 204 for successful delete`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    every { languageService.deleteLanguage(1) } returns Unit

    val response = client.delete("/api/languages/1")

    assertEquals(HttpStatusCode.NoContent, response.status)
    verify { languageService.deleteLanguage(1) }
  }

  @Test
  fun `DELETE api-languages-id returns 409 for language in use`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    every { languageService.deleteLanguage(1) } throws EntityInUseException("Language", "has books")

    val response = client.delete("/api/languages/1")

    assertEquals(HttpStatusCode.Conflict, response.status)
  }

  @Test
  fun `DELETE api-languages-id returns 404 for non-existent language`() =
      testApplicationWithRoutes {
        val client = createClient { install(ClientContentNegotiation) { json() } }
        every { languageService.deleteLanguage(999) } throws
            EntityNotFoundException("Language", 999)

        val response = client.delete("/api/languages/999")

        assertEquals(HttpStatusCode.NotFound, response.status)
      }

  @Test
  fun `GET api-languages-id-dicts returns dictionaries`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val dictionaries =
        listOf(
            DictionaryDto(
                id = 1,
                language_id = 1,
                ld_use_for = "terms",
                ld_type = "url",
                ld_dict_uri = "https://dict.com",
                ld_is_active = true,
                ld_sort_order = 1,
            ),
        )
    every { dictionaryService.getDictionariesForLanguage(1) } returns dictionaries

    val response = client.get("/api/languages/1/dicts")

    assertEquals(HttpStatusCode.OK, response.status)
  }

  @Test
  fun `POST api-languages-id-dicts returns 201 for valid dictionary`() = testApplicationWithRoutes {
    val client = createClient { install(ClientContentNegotiation) { json() } }
    val dto =
        CreateDictionaryDto(
            ld_use_for = "terms",
            ld_type = "url",
            ld_dict_uri = "https://dict.com",
            ld_sort_order = 1,
        )
    val created =
        DictionaryDto(
            id = 1,
            language_id = 1,
            ld_use_for = "terms",
            ld_type = "url",
            ld_dict_uri = "https://dict.com",
            ld_is_active = true,
            ld_sort_order = 1,
        )
    every { dictionaryService.addDictionary(1, dto) } returns created

    val response =
        client.post("/api/languages/1/dicts") {
          contentType(ContentType.Application.Json)
          setBody(dto)
        }

    assertEquals(HttpStatusCode.Created, response.status)
  }

  @Test
  fun `PATCH api-languages-id-dicts-dictId returns 200 for valid update`() =
      testApplicationWithRoutes {
        val client = createClient { install(ClientContentNegotiation) { json() } }
        val dto = UpdateDictionaryDto(ld_dict_uri = "https://new.com")
        val updated =
            DictionaryDto(
                id = 1,
                language_id = 1,
                ld_use_for = "terms",
                ld_type = "url",
                ld_dict_uri = "https://new.com",
                ld_is_active = true,
                ld_sort_order = 1,
            )
        every { dictionaryService.updateDictionary(2, 1, dto) } returns updated

        val response =
            client.patch("/api/languages/1/dicts/2") {
              contentType(ContentType.Application.Json)
              setBody(dto)
            }

        assertEquals(HttpStatusCode.OK, response.status)
      }

  @Test
  fun `PATCH api-languages-id-dicts-dictId returns 404 for non-existent dictionary`() =
      testApplicationWithRoutes {
        val client = createClient { install(ClientContentNegotiation) { json() } }
        val dto = UpdateDictionaryDto(ld_dict_uri = "https://new.com")
        every { dictionaryService.updateDictionary(999, 1, dto) } returns null

        val response =
            client.patch("/api/languages/1/dicts/999") {
              contentType(ContentType.Application.Json)
              setBody(dto)
            }

        assertEquals(HttpStatusCode.NotFound, response.status)
      }

  @Test
  fun `DELETE api-languages-id-dicts-dictId returns 204 for successful delete`() =
      testApplicationWithRoutes {
        val client = createClient { install(ClientContentNegotiation) { json() } }
        every { dictionaryService.deleteDictionary(2, 1) } returns true

        val response = client.delete("/api/languages/1/dicts/2")

        assertEquals(HttpStatusCode.NoContent, response.status)
      }

  @Test
  fun `DELETE api-languages-id-dicts-dictId returns 404 for non-existent dictionary`() =
      testApplicationWithRoutes {
        val client = createClient { install(ClientContentNegotiation) { json() } }
        every { dictionaryService.deleteDictionary(999, 1) } returns false

        val response = client.delete("/api/languages/1/dicts/999")

        assertEquals(HttpStatusCode.NotFound, response.status)
      }
}
