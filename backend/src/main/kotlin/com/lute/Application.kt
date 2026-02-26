package com.lute

import com.lute.application.exceptions.ApplicationException
import com.lute.application.exceptions.DuplicateEntityException
import com.lute.application.exceptions.EntityInUseException
import com.lute.application.exceptions.EntityNotFoundException
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
    exception<EntityNotFoundException> { call, cause ->
      call.respond(HttpStatusCode.NotFound, ErrorResponse(cause.message ?: "Entity not found"))
    }

    exception<DuplicateEntityException> { call, cause ->
      call.respond(HttpStatusCode.Conflict, ErrorResponse(cause.message ?: "Entity already exists"))
    }

    exception<EntityInUseException> { call, cause ->
      call.respond(HttpStatusCode.Conflict, ErrorResponse(cause.message ?: "Entity is in use"))
    }

    exception<ValidationException> { call, cause ->
      val errors = cause.errors.map { (field, message) -> ValidationError(field, message) }
      call.respond(HttpStatusCode.BadRequest, ValidationErrorResponse(errors))
    }

    exception<ApplicationException> { call, cause ->
      call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Application error"))
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

    val bookRoutes = ServiceLocator.bookRoutes
    bookRoutes.register(this)

    val termRoutes = ServiceLocator.termRoutes
    termRoutes.register(this)

    val readingRoutes = ServiceLocator.readingRoutes
    readingRoutes.register(this)

    val statsRoute = ServiceLocator.statsRoute
    statsRoute.register(this)

    val settingsRoute = ServiceLocator.settingsRoute
    settingsRoute.register(this)

    val themeRoute = ServiceLocator.themeRoute
    themeRoute.register(this)

    val hotkeyRoute = ServiceLocator.hotkeyRoute
    hotkeyRoute.register(this)
  }
}

fun main() {
  embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}
