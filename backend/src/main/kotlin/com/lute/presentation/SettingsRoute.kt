package com.lute.presentation

import com.lute.db.repositories.SettingsRepository
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

class SettingsRoute(private val settingsRepository: SettingsRepository) {
  fun register(route: Route) {
    route.get("/api/settings") {
      val all = settingsRepository.getAll()
      val response =
          all.mapValues { (_, setting) ->
            SettingValue(value = setting.value, keyType = setting.keyType)
          }
      call.respond(response)
    }
    route.put("/api/settings/{key}") {
      val key = call.parameters["key"]
      if (key == null) {
        call.respond(BadRequestResponse("Missing key parameter"))
        return@put
      }
      val body = call.receive<UpdateSettingRequest>()
      settingsRepository.set(key, body.value, body.keyType ?: "str")
      call.respond(SuccessResponse(true))
    }
  }
}

@Serializable private data class SettingValue(val value: String?, val keyType: String)

@Serializable
private data class UpdateSettingRequest(val value: String, val keyType: String? = null)

@Serializable private data class SuccessResponse(val success: Boolean)

@Serializable private data class BadRequestResponse(val error: String)
