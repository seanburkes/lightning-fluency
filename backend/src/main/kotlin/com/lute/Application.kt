package com.lute

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable data class HealthResponse(val status: String)

fun Application.module() {
  install(ContentNegotiation) {
    json(
        Json { prettyPrint = true },
    )
  }

  install(StatusPages) {
    exception<Throwable> { call, cause ->
      println("Unhandled exception: ${cause.message}")
      cause.printStackTrace()
      call.respond(
          HttpStatusCode.InternalServerError,
          mapOf("error" to "Internal server error"),
      )
    }
  }

  routing { get("/api/health") { call.respond(HealthResponse(status = "ok")) } }
}

fun main() {
  embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}
