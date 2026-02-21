package com.lute

import com.lute.application.exceptions.DuplicateLanguageException
import com.lute.application.exceptions.LanguageInUseException
import com.lute.application.exceptions.LanguageNotFoundException
import com.lute.application.exceptions.ValidationException
import com.lute.db.DatabaseFactory
import com.lute.db.migrations.MigrationException
import com.lute.db.migrations.MigrationManager
import com.lute.di.ServiceLocator
import com.lute.domain.ErrorResponse
import com.lute.dtos.ValidationError
import com.lute.dtos.ValidationErrorResponse
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
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Application")

fun Application.module() {
  environment.monitor.subscribe(ApplicationStopped) {
    logger.info("Shutting down database connections")
    DatabaseFactory.shutdown()
  }

  try {
    val db = DatabaseFactory.database ?: DatabaseFactory.init()
    logger.info("Database initialized successfully")
    MigrationManager(db).runMigrations()
    logger.info("Migrations completed successfully")
  } catch (e: MigrationException) {
    logger.error("Migration failed: ${e.migrationName}", e)
    throw e
  } catch (e: Exception) {
    logger.error("Failed to initialize database", e)
    throw e
  }

  install(ContentNegotiation) { json(Json { prettyPrint = true }) }

  install(StatusPages) {
    exception<LanguageNotFoundException> { call, cause ->
      call.respond(HttpStatusCode.NotFound, ErrorResponse(cause.message ?: "Language not found"))
    }

    exception<LanguageInUseException> { call, cause ->
      call.respond(HttpStatusCode.Conflict, ErrorResponse(cause.message ?: "Language is in use"))
    }

    exception<DuplicateLanguageException> { call, cause ->
      call.respond(
          HttpStatusCode.Conflict,
          ErrorResponse(cause.message ?: "Language with this name already exists"),
      )
    }

    exception<ValidationException> { call, cause ->
      val errors = cause.errors.map { (field, message) -> ValidationError(field, message) }
      call.respond(HttpStatusCode.BadRequest, ValidationErrorResponse(errors))
    }

    exception<Throwable> { call, cause ->
      logger.error("Unhandled exception", cause)
      call.respond(
          HttpStatusCode.InternalServerError,
          ErrorResponse(error = "Internal server error"),
      )
    }
  }

  routing {
    val healthRoute = ServiceLocator.healthRoute
    healthRoute.register(this)

    val languageRoutes = ServiceLocator.languageRoutes
    languageRoutes.register(this)
  }
}

fun main() {
  embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}
