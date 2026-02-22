package com.lute.presentation

import com.lute.application.BookService
import com.lute.domain.ErrorResponse
import com.lute.dtos.AddTagToBookDto
import com.lute.dtos.CreateBookDto
import com.lute.dtos.UpdateBookDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*

class BookRoutes(private val bookService: BookService) {
  private fun ApplicationCall.parseId(param: String): Long? = parameters[param]?.toLongOrNull()

  private fun ApplicationCall.parseBoolean(param: String): Boolean? =
      parameters[param]?.toBooleanStrictOrNull()

  fun register(route: Route) {
    route.route("/api/books") {
      get {
        val languageId = call.parseId("language_id")
        val archived = call.parseBoolean("archived")
        call.respond(bookService.getAllBooks(languageId, archived))
      }

      post {
        val dto = call.receive<CreateBookDto>()
        val book = bookService.createBook(dto)
        call.respond(HttpStatusCode.Created, book)
      }

      route("/{id}") {
        get {
          val id = call.parseId("id")
          if (id == null) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid book ID"))
            return@get
          }

          val book = bookService.getBookById(id)
          if (book == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "Book not found"))
          } else {
            call.respond(book)
          }
        }

        patch {
          val id = call.parseId("id")
          if (id == null) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid book ID"))
            return@patch
          }

          val dto = call.receive<UpdateBookDto>()
          val book = bookService.updateBook(id, dto)
          if (book == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "Book not found"))
          } else {
            call.respond(book)
          }
        }

        delete {
          val id = call.parseId("id")
          if (id == null) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid book ID"))
            return@delete
          }

          val deleted = bookService.deleteBook(id)
          if (deleted) {
            call.respond(HttpStatusCode.NoContent)
          } else {
            call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "Book not found"))
          }
        }

        route("/pages") {
          get {
            val id = call.parseId("id")
            if (id == null) {
              call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid book ID"))
              return@get
            }

            val pages = bookService.getBookPages(id)
            call.respond(pages)
          }
        }

        route("/tags") {
          get {
            val id = call.parseId("id")
            if (id == null) {
              call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid book ID"))
              return@get
            }

            val tags = bookService.getTagsForBook(id)
            call.respond(mapOf("tags" to tags))
          }

          post {
            val id = call.parseId("id")
            if (id == null) {
              call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid book ID"))
              return@post
            }

            val dto = call.receive<AddTagToBookDto>()
            bookService.addTagToBook(id, dto.tag_id)
            call.respond(HttpStatusCode.Created)
          }

          route("/{tagId}") {
            delete {
              val id = call.parseId("id")
              if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid book ID"))
                return@delete
              }

              val tagId = call.parseId("tagId")
              if (tagId == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid tag ID"))
                return@delete
              }

              bookService.removeTagFromBook(id, tagId)
              call.respond(HttpStatusCode.NoContent)
            }
          }
        }
      }
    }
  }
}
