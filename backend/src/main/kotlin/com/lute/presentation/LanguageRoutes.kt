package com.lute.presentation

import com.lute.application.DictionaryService
import com.lute.application.LanguageService
import com.lute.dtos.CreateDictionaryDto
import com.lute.dtos.CreateLanguageDto
import com.lute.dtos.UpdateDictionaryDto
import com.lute.dtos.UpdateLanguageDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*

class LanguageRoutes(
    private val languageService: LanguageService,
    private val dictionaryService: DictionaryService,
) {
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
          val id = call.parseIdOrBadRequest("id", "language") ?: return@get
          val language =
              call.requireEntity(id, "Language", languageService::getLanguageById) ?: return@get
          call.respond(language)
        }

        patch {
          val id = call.parseIdOrBadRequest("id", "language") ?: return@patch
          val dto = call.receive<UpdateLanguageDto>()
          val language =
              call.requireEntity(id, "Language") { languageService.updateLanguage(it, dto) }
                  ?: return@patch
          call.respond(language)
        }

        delete {
          val id = call.parseIdOrBadRequest("id", "language") ?: return@delete
          languageService.deleteLanguage(id)
          call.respond(HttpStatusCode.NoContent)
        }

        route("/dicts") {
          get {
            val langId = call.parseIdOrBadRequest("id", "language") ?: return@get
            call.respond(dictionaryService.getDictionariesForLanguage(langId))
          }

          post {
            val langId = call.parseIdOrBadRequest("id", "language") ?: return@post
            val dto = call.receive<CreateDictionaryDto>()
            val dictionary = dictionaryService.addDictionary(langId, dto)
            call.respond(HttpStatusCode.Created, dictionary)
          }

          route("/{dictId}") {
            patch {
              val langId = call.parseIdOrBadRequest("id", "language") ?: return@patch
              val dictId = call.parseIdOrBadRequest("dictId", "dictionary") ?: return@patch
              val dto = call.receive<UpdateDictionaryDto>()
              val dictionary = dictionaryService.updateDictionary(dictId, langId, dto)
              if (dictionary == null) {
                call.respondNotFound("Dictionary")
              } else {
                call.respond(dictionary)
              }
            }

            delete {
              val langId = call.parseIdOrBadRequest("id", "language") ?: return@delete
              val dictId = call.parseIdOrBadRequest("dictId", "dictionary") ?: return@delete
              val deleted = dictionaryService.deleteDictionary(dictId, langId)
              if (deleted) {
                call.respond(HttpStatusCode.NoContent)
              } else {
                call.respondNotFound("Dictionary")
              }
            }
          }
        }
      }
    }
  }
}
