package com.lute.presentation

import com.lute.application.HealthService
import io.ktor.server.response.*
import io.ktor.server.routing.*

class HealthRoute(private val healthService: HealthService) {
  fun register(route: Route) {
    route.get("/api/health") { call.respond(healthService.check()) }
  }
}
