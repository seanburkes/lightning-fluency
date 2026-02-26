package com.lute.presentation

import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

class ThemeRoute {
  fun register(route: Route) {
    route.get("/api/themes") {
      call.respond(listOf(ThemeResp("cerberus"), ThemeResp("skeleton"), ThemeResp("wintry")))
    }
    route.get("/api/themes/{name}/css") {
      val name = call.parameters["name"]
      if (name == null) {
        call.respond(ThemeCssResp(""))
        return@get
      }
      val css = getThemeCss(name)
      call.respond(ThemeCssResp(css ?: ""))
    }
  }

  private fun getThemeCss(name: String): String? {
    return when (name) {
      "cerberus" -> ""
      "skeleton" -> ""
      "wintry" -> ""
      else -> null
    }
  }
}

@Serializable private data class ThemeResp(val name: String)

@Serializable private data class ThemeCssResp(val css: String)
