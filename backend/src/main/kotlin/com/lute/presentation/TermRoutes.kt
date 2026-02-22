package com.lute.presentation

import com.lute.application.TermService
import com.lute.domain.ErrorResponse
import com.lute.dtos.BulkOperationDto
import com.lute.dtos.CreateTermDto
import com.lute.dtos.UpdateTermDto
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.ByteArrayContent
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.*

class TermRoutes(private val termService: TermService) {
  private fun ApplicationCall.parseId(param: String): Long? = parameters[param]?.toLongOrNull()

  private fun ApplicationCall.parseInt(param: String): Int? = parameters[param]?.toIntOrNull()

  fun register(route: Route) {
    route.route("/api/terms") {
      get {
        val languageId = call.parseId("language_id")
        val status = call.parseInt("status")
        val limit = call.parseInt("limit") ?: 100
        val offset = call.parseInt("offset") ?: 0

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
          val status = call.parseInt("status")
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
          val languageId = call.parseId("language_id")
          if (languageId == null) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(error = "language_id is required"),
            )
            return@post
          }

          val text = call.receiveText()
          val result = termService.importFromCsv(text, languageId)
          call.respond(result)
        }
      }

      route("/{id}") {
        get {
          val id = call.parseId("id")
          if (id == null) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid term ID"))
            return@get
          }

          val term = termService.getTermById(id)
          if (term == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "Term not found"))
          } else {
            call.respond(term)
          }
        }

        patch {
          val id = call.parseId("id")
          if (id == null) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid term ID"))
            return@patch
          }

          val dto = call.receive<UpdateTermDto>()
          val term = termService.updateTerm(id, dto)
          if (term == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "Term not found"))
          } else {
            call.respond(term)
          }
        }

        delete {
          val id = call.parseId("id")
          if (id == null) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid term ID"))
            return@delete
          }

          val deleted = termService.deleteTerm(id)
          if (deleted) {
            call.respond(HttpStatusCode.NoContent)
          } else {
            call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "Term not found"))
          }
        }

        route("/parents") {
          get {
            val id = call.parseId("id")
            if (id == null) {
              call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid term ID"))
              return@get
            }

            try {
              val parents = termService.getParents(id)
              call.respond(parents)
            } catch (e: Exception) {
              call.respond(HttpStatusCode.NotFound, ErrorResponse(error = e.message ?: "Error"))
            }
          }

          post {
            val id = call.parseId("id")
            if (id == null) {
              call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid term ID"))
              return@post
            }

            val parentId = call.parseId("parent_id")
            if (parentId == null) {
              call.respond(
                  HttpStatusCode.BadRequest,
                  ErrorResponse(error = "parent_id is required"),
              )
              return@post
            }

            try {
              termService.addParent(id, parentId)
              call.respond(HttpStatusCode.Created)
            } catch (e: Exception) {
              call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = e.message ?: "Error"))
            }
          }

          route("/{parentId}") {
            delete {
              val id = call.parseId("id")
              val parentId = call.parseId("parentId")
              if (id == null || parentId == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid ID"))
                return@delete
              }

              termService.removeParent(id, parentId)
              call.respond(HttpStatusCode.NoContent)
            }
          }
        }
      }
    }
  }
}
