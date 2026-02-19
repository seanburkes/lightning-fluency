package com.lute

import com.lute.db.DatabaseFactory
import com.lute.db.migrations.MigrationManager
import com.lute.di.ServiceLocator
import com.lute.domain.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Application.module() {
  val db = DatabaseFactory.database ?: DatabaseFactory.init()
  MigrationManager(db).runMigrations()

  install(ContentNegotiation) { json(Json { prettyPrint = true }) }

  install(StatusPages) {
    exception<Throwable> { call, cause ->
      println("Unhandled exception: ${cause.message}")
      cause.printStackTrace()
      call.respond(
          HttpStatusCode.InternalServerError,
          ErrorResponse(error = "Internal server error"),
      )
    }
  }

  routing {
    val healthRoute = ServiceLocator.healthRoute
    healthRoute.register(this)
  }
}

fun main() {
  embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}
