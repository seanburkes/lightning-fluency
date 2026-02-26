package com.lute.presentation

import com.lute.db.repositories.SettingsRepository
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

class HotkeyRoute(private val settingsRepository: SettingsRepository) {
  fun register(route: Route) {
    route.get("/api/hotkeys") {
      val all = settingsRepository.getAll()
      call.respond(
          listOf(
              HotkeyResp(
                  "next_page",
                  all["HotkeyNextPage"]?.value ?: "ctrl+right",
                  "Go to next page",
              ),
              HotkeyResp(
                  "prev_page",
                  all["HotkeyPrevPage"]?.value ?: "ctrl+left",
                  "Go to previous page",
              ),
              HotkeyResp(
                  "show_popup",
                  all["HotkeyShowPopup"]?.value ?: "ctrl+space",
                  "Show term popup",
              ),
              HotkeyResp(
                  "mark_known",
                  all["HotkeyMarkKnown"]?.value ?: "k",
                  "Mark term as known",
              ),
              HotkeyResp(
                  "mark_unknown",
                  all["HotkeyMarkUnknown"]?.value ?: "u",
                  "Mark term as unknown",
              ),
          ),
      )
    }
    route.put("/api/hotkeys") {
      val body = call.receive<List<HotkeyReq>>()
      body.forEach { hotkey ->
        settingsRepository.set(actionToKey(hotkey.action), hotkey.hotkey, "str")
      }
      call.respond(SimpleResp(true))
    }
    route.post("/api/hotkeys/reset") {
      DEFAULT_HOTKEYS.forEach { (key, value) -> settingsRepository.set(key, value, "str") }
      call.respond(SimpleResp(true))
    }
  }

  companion object {
    private val DEFAULT_HOTKEYS =
        mapOf(
            "HotkeyNextPage" to "ctrl+right",
            "HotkeyPrevPage" to "ctrl+left",
            "HotkeyShowPopup" to "ctrl+space",
            "HotkeyMarkKnown" to "k",
            "HotkeyMarkUnknown" to "u",
        )

    private val ACTION_TO_KEY =
        mapOf(
            "next_page" to "HotkeyNextPage",
            "prev_page" to "HotkeyPrevPage",
            "show_popup" to "HotkeyShowPopup",
            "mark_known" to "HotkeyMarkKnown",
            "mark_unknown" to "HotkeyMarkUnknown",
        )

    private fun actionToKey(action: String): String =
        ACTION_TO_KEY[action] ?: "Hotkey${action.replaceFirstChar { it.uppercase() }}"
  }
}

@Serializable
private data class HotkeyResp(val action: String, val hotkey: String, val description: String)

@Serializable
private data class HotkeyReq(val action: String, val hotkey: String, val description: String)

@Serializable private data class SimpleResp(val success: Boolean)
