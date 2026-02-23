package com.lute.presentation

import com.lute.application.BookService
import com.lute.dtos.AddTagToBookDto
import com.lute.dtos.CreateBookDto
import com.lute.dtos.UpdateBookDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*

class BookRoutes(private val bookService: BookService) {
  fun register(route: Route) {
    route.route("/api/books") {
      get {
        val languageId = call.parseId("language_id")
        val archived = call.parseBooleanParam("archived")
        call.respond(bookService.getAllBooks(languageId, archived))
      }

      post {
        val dto = call.receive<CreateBookDto>()
        val book = bookService.createBook(dto)
        call.respond(HttpStatusCode.Created, book)
      }

      route("/{id}") {
        get {
          val id = call.parseIdOrBadRequest("id", "book") ?: return@get
          val book = call.requireEntity(id, "Book", bookService::getBookById) ?: return@get
          call.respond(book)
        }

        patch {
          val id = call.parseIdOrBadRequest("id", "book") ?: return@patch
          val dto = call.receive<UpdateBookDto>()
          val book =
              call.requireEntity(id, "Book") { bookService.updateBook(it, dto) } ?: return@patch
          call.respond(book)
        }

        delete {
          val id = call.parseIdOrBadRequest("id", "book") ?: return@delete
          val deleted = bookService.deleteBook(id)
          if (deleted) {
            call.respond(HttpStatusCode.NoContent)
          } else {
            call.respondNotFound("Book")
          }
        }

        route("/pages") {
          get {
            val id = call.parseIdOrBadRequest("id", "book") ?: return@get
            val pages = bookService.getBookPages(id)
            call.respond(pages)
          }
        }

        route("/tags") {
          get {
            val id = call.parseIdOrBadRequest("id", "book") ?: return@get
            val tags = bookService.getTagsForBook(id)
            call.respond(mapOf("tags" to tags))
          }

          post {
            val id = call.parseIdOrBadRequest("id", "book") ?: return@post
            val dto = call.receive<AddTagToBookDto>()
            bookService.addTagToBook(id, dto.tag_id)
            call.respond(HttpStatusCode.Created)
          }

          route("/{tagId}") {
            delete {
              val id = call.parseIdOrBadRequest("id", "book") ?: return@delete
              val tagId = call.parseIdOrBadRequest("tagId", "tag") ?: return@delete
              bookService.removeTagFromBook(id, tagId)
              call.respond(HttpStatusCode.NoContent)
            }
          }
        }
      }
    }
  }
}
