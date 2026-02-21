package com.lute.parse

import org.slf4j.LoggerFactory

object CharacterSubstitution {
  private val logger = LoggerFactory.getLogger(CharacterSubstitution::class.java)

  fun apply(text: String, substitutionConfig: String?): String {
    if (substitutionConfig.isNullOrBlank()) return text

    val pairs = parse(substitutionConfig)
    var result = text
    for ((from, to) in pairs) {
      result = result.replace(from, to)
    }
    return result
  }

  fun parse(config: String): List<Pair<String, String>> {
    if (config.isBlank()) return emptyList()

    return config.split("|").mapNotNull { entry ->
      val parts = entry.split("=", limit = 2)
      if (parts.size >= 2) {
        Pair(parts[0], parts[1])
      } else {
        logger.warn("Invalid character substitution entry (missing '='): '$entry'")
        null
      }
    }
  }
}
