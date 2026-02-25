package com.lute.presentation

import com.lute.application.AudioService
import com.lute.application.BookService
import com.lute.domain.ErrorResponse
import com.lute.dtos.AddTagToBookDto
import com.lute.dtos.CreateBookDto
import com.lute.dtos.UpdateBookDto
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.forEachPart
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.*
import io.ktor.utils.io.readAvailable
import java.io.ByteArrayOutputStream

class BookRoutes(private val bookService: BookService, private val audioService: AudioService) {
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
            audioService.deleteAudio(id)
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

        route("/audio") {
          get {
            val id = call.parseIdOrBadRequest("id", "book") ?: return@get
            val audioFile = audioService.getAudio(id)
            if (audioFile == null) {
              call.respondNotFound("Audio")
              return@get
            }
            val bytes = audioFile.readBytes()
            call.respondBytes(bytes, ContentType.Application.OctetStream)
          }

          post {
            val id = call.parseIdOrBadRequest("id", "book") ?: return@post
            val book = call.requireEntity(id, "Book", bookService::getBookById) ?: return@post

            val multipart = call.receiveMultipart()
            var filename: String? = null
            var fileBytes: ByteArray? = null

            multipart.forEachPart { part ->
              when (part) {
                is io.ktor.http.content.PartData.FileItem -> {
                  filename = part.originalFileName
                  val channel = part.provider()
                  val baos = ByteArrayOutputStream()
                  val buffer = ByteArray(8192)
                  while (!channel.isClosedForRead) {
                    val read = channel.readAvailable(buffer)
                    if (read > 0) {
                      baos.write(buffer, 0, read)
                    }
                  }
                  fileBytes = baos.toByteArray()
                }
                else -> {}
              }
              part.dispose()
            }

            if (filename == null || fileBytes == null) {
              call.respond(
                  HttpStatusCode.BadRequest,
                  ErrorResponse(error = "No audio file provided"),
              )
              return@post
            }

            val audioFilename = audioService.storeAudio(id, filename!!, fileBytes!!.inputStream())
            if (audioFilename == null) {
              call.respondNotFound("Book")
              return@post
            }

            call.respond(HttpStatusCode.Created, mapOf("filename" to audioFilename))
          }

          delete {
            val id = call.parseIdOrBadRequest("id", "book") ?: return@delete
            val deleted = audioService.deleteAudio(id)
            if (deleted) {
              call.respond(HttpStatusCode.NoContent)
            } else {
              call.respondNotFound("Audio")
            }
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
