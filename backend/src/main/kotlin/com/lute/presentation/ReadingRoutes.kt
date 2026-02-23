package com.lute.presentation

import com.lute.application.ReadingService
import com.lute.dtos.SaveCurrentPageDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*

class ReadingRoutes(private val readingService: ReadingService) {
  fun register(route: Route) {
    route.route("/api/read/{bookId}") {
      route("/pages") {
        get("/{pageNum}") {
          val bookId = call.parseIdOrBadRequest("bookId", "book") ?: return@get
          val pageNum = call.parseIntOrBadRequest("pageNum", "page") ?: return@get

          val page = readingService.getPage(bookId, pageNum)
          if (page != null) {
            call.respond(page)
          } else {
            call.respondNotFound("Page")
          }
        }

        get("/next") {
          val bookId = call.parseIdOrBadRequest("bookId", "book") ?: return@get
          val currentPage = call.parseIntParam("current") ?: 1

          val nextPage = readingService.getNextPage(bookId, currentPage)
          if (nextPage != null) {
            call.respond(mapOf("next_page" to nextPage))
          } else {
            call.respond(HttpStatusCode.NoContent)
          }
        }

        get("/prev") {
          val bookId = call.parseIdOrBadRequest("bookId", "book") ?: return@get
          val currentPage = call.parseIntParam("current") ?: 1

          val prevPage = readingService.getPreviousPage(bookId, currentPage)
          if (prevPage != null) {
            call.respond(mapOf("previous_page" to prevPage))
          } else {
            call.respond(HttpStatusCode.NoContent)
          }
        }
      }

      get("/current") {
        val bookId = call.parseIdOrBadRequest("bookId", "book") ?: return@get

        val currentPage = readingService.getCurrentPage(bookId)
        if (currentPage != null) {
          call.respond(mapOf("current_page" to currentPage))
        } else {
          call.respond(HttpStatusCode.NoContent)
        }
      }

      post("/current") {
        val bookId = call.parseIdOrBadRequest("bookId", "book") ?: return@post
        val dto = call.receive<SaveCurrentPageDto>()

        readingService.saveCurrentPage(bookId, dto.page_num)
        call.respond(HttpStatusCode.OK)
      }
    }
  }
}
