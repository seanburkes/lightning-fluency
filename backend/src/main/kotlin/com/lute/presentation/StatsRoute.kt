package com.lute.presentation

import com.lute.application.StatsService
import io.ktor.server.response.*
import io.ktor.server.routing.*

class StatsRoute(private val statsService: StatsService) {
  fun register(route: Route) {
    route.get("/api/stats") { call.respond(statsService.getStats()) }
  }
}
