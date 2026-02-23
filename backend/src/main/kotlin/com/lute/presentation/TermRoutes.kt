package com.lute.presentation

import com.lute.application.TermService
import com.lute.dtos.BulkOperationDto
import com.lute.dtos.CreateTermDto
import com.lute.dtos.UpdateTermDto
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.ByteArrayContent
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.*

class TermRoutes(private val termService: TermService) {
  fun register(route: Route) {
    route.route("/api/terms") {
      get {
        val languageId = call.parseId("language_id")
        val status = call.parseIntParam("status")
        val limit = call.parseIntParam("limit") ?: 100
        val offset = call.parseIntParam("offset") ?: 0

        val search = call.parameters["search"]
        if (search != null) {
          call.respond(termService.searchTerms(search, languageId, status))
        } else {
          call.respond(termService.getAllTerms(languageId, status, limit, offset))
        }
      }

      post {
        val dto = call.receive<CreateTermDto>()
        val term = termService.createTerm(dto)
        call.respond(HttpStatusCode.Created, term)
      }

      route("/bulk") {
        post {
          val dto = call.receive<BulkOperationDto>()
          val result =
              termService.bulkOperation(
                  dto.operation,
                  dto.term_ids,
                  dto.status,
                  dto.tag_ids,
              )
          call.respond(result)
        }
      }

      route("/export") {
        get {
          val languageId = call.parseId("language_id")
          val status = call.parseIntParam("status")
          val csv = termService.exportToCsv(languageId, status)
          call.respond(
              ByteArrayContent(
                  bytes = csv,
                  contentType = io.ktor.http.ContentType.Text.CSV,
                  status = HttpStatusCode.OK,
              ),
          )
        }
      }

      route("/import") {
        post {
          val languageId = call.parseIdOrBadRequest("language_id", "language") ?: return@post
          val text = call.receiveText()
          val result = termService.importFromCsv(text, languageId)
          call.respond(result)
        }
      }

      route("/{id}") {
        get {
          val id = call.parseIdOrBadRequest("id", "term") ?: return@get
          val term = call.requireEntity(id, "Term", termService::getTermById) ?: return@get
          call.respond(term)
        }

        patch {
          val id = call.parseIdOrBadRequest("id", "term") ?: return@patch
          val dto = call.receive<UpdateTermDto>()
          val term =
              call.requireEntity(id, "Term") { termService.updateTerm(it, dto) } ?: return@patch
          call.respond(term)
        }

        delete {
          val id = call.parseIdOrBadRequest("id", "term") ?: return@delete
          val deleted = termService.deleteTerm(id)
          if (deleted) {
            call.respond(HttpStatusCode.NoContent)
          } else {
            call.respondNotFound("Term")
          }
        }

        route("/parents") {
          get {
            val id = call.parseIdOrBadRequest("id", "term") ?: return@get
            try {
              val parents = termService.getParents(id)
              call.respond(parents)
            } catch (e: Exception) {
              call.respondNotFound("Term")
            }
          }

          post {
            val id = call.parseIdOrBadRequest("id", "term") ?: return@post
            val parentId = call.parseIdOrBadRequest("parent_id", "parent term") ?: return@post
            try {
              termService.addParent(id, parentId)
              call.respond(HttpStatusCode.Created)
            } catch (e: Exception) {
              call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Error")))
            }
          }

          route("/{parentId}") {
            delete {
              val id = call.parseIdOrBadRequest("id", "term") ?: return@delete
              val parentId = call.parseIdOrBadRequest("parentId", "parent term") ?: return@delete
              termService.removeParent(id, parentId)
              call.respond(HttpStatusCode.NoContent)
            }
          }
        }
      }
    }
  }
}
