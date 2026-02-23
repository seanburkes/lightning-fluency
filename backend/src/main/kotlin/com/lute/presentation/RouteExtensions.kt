package com.lute.presentation

import com.lute.domain.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.respond

suspend fun ApplicationCall.parseIdOrBadRequest(param: String, entityName: String): Long? {
  val id = parameters[param]?.toLongOrNull()
  if (id == null) {
    respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid $entityName ID"))
  }
  return id
}

suspend fun ApplicationCall.parseIntOrBadRequest(param: String, fieldName: String): Int? {
  val value = parameters[param]?.toIntOrNull()
  if (value == null && parameters.contains(param)) {
    respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid $fieldName"))
  }
  return value
}

fun ApplicationCall.parseBooleanParam(param: String): Boolean? =
    parameters[param]?.toBooleanStrictOrNull()

fun ApplicationCall.parseId(param: String): Long? = parameters[param]?.toLongOrNull()

fun ApplicationCall.parseIntParam(param: String): Int? = parameters[param]?.toIntOrNull()

suspend inline fun <reified T> ApplicationCall.requireEntity(
    id: Long,
    entityName: String,
    fetcher: (Long) -> T?,
): T? {
  val entity = fetcher(id)
  if (entity == null) {
    respond(HttpStatusCode.NotFound, ErrorResponse(error = "$entityName not found"))
  }
  return entity
}

suspend fun ApplicationCall.respondNotFound(entityName: String) {
  respond(HttpStatusCode.NotFound, ErrorResponse(error = "$entityName not found"))
}
