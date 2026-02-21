package com.lute.presentation

import com.lute.application.DictionaryService
import com.lute.application.LanguageService
import com.lute.domain.ErrorResponse
import com.lute.dtos.CreateDictionaryDto
import com.lute.dtos.CreateLanguageDto
import com.lute.dtos.UpdateDictionaryDto
import com.lute.dtos.UpdateLanguageDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*

class LanguageRoutes(
    private val languageService: LanguageService,
    private val dictionaryService: DictionaryService,
) {
  private fun ApplicationCall.parseId(param: String): Long? = parameters[param]?.toLongOrNull()

  fun register(route: Route) {
    route.route("/api/languages") {
      get { call.respond(languageService.getAllLanguages()) }

      post {
        val dto = call.receive<CreateLanguageDto>()
        val language = languageService.createLanguage(dto)
        call.respond(HttpStatusCode.Created, language)
      }

      route("/{id}") {
        get {
          val id = call.parseId("id")
          if (id == null) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid language ID"))
            return@get
          }

          val language = languageService.getLanguageById(id)
          if (language == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "Language not found"))
          } else {
            call.respond(language)
          }
        }

        patch {
          val id = call.parseId("id")
          if (id == null) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid language ID"))
            return@patch
          }

          val dto = call.receive<UpdateLanguageDto>()
          val language = languageService.updateLanguage(id, dto)
          if (language == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "Language not found"))
          } else {
            call.respond(language)
          }
        }

        delete {
          val id = call.parseId("id")
          if (id == null) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid language ID"))
            return@delete
          }

          languageService.deleteLanguage(id)
          call.respond(HttpStatusCode.NoContent)
        }

        route("/dicts") {
          get {
            val langId = call.parseId("id")
            if (langId == null) {
              call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid language ID"))
              return@get
            }

            call.respond(dictionaryService.getDictionariesForLanguage(langId))
          }

          post {
            val langId = call.parseId("id")
            if (langId == null) {
              call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid language ID"))
              return@post
            }

            val dto = call.receive<CreateDictionaryDto>()
            val dictionary = dictionaryService.addDictionary(langId, dto)
            call.respond(HttpStatusCode.Created, dictionary)
          }

          route("/{dictId}") {
            patch {
              val langId = call.parseId("id")
              val dictId = call.parseId("dictId")
              if (langId == null || dictId == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid ID"))
                return@patch
              }

              val dto = call.receive<UpdateDictionaryDto>()
              val dictionary = dictionaryService.updateDictionary(dictId, langId, dto)
              if (dictionary == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "Dictionary not found"))
              } else {
                call.respond(dictionary)
              }
            }

            delete {
              val langId = call.parseId("id")
              val dictId = call.parseId("dictId")
              if (langId == null || dictId == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid ID"))
                return@delete
              }

              val deleted = dictionaryService.deleteDictionary(dictId, langId)
              if (deleted) {
                call.respond(HttpStatusCode.NoContent)
              } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "Dictionary not found"))
              }
            }
          }
        }
      }
    }
  }
}
