package com.lute.parse

import java.util.concurrent.ConcurrentHashMap

object SentenceSplitter {
  private const val DEFAULT_SPLIT_CHARS = ".!?:"
  private val sentenceEndCache = ConcurrentHashMap<String, Regex>()
  private val tokenPatternCache = ConcurrentHashMap<String, Regex>()

  fun containsSentenceEnd(text: String, splitSentencesRegex: String?): Boolean {
    val chars = splitSentencesRegex?.takeIf { it.isNotBlank() } ?: DEFAULT_SPLIT_CHARS
    val pattern = sentenceEndCache.getOrPut(chars) { Regex("[${Regex.escape(chars)}]") }
    return pattern.containsMatchIn(text)
  }

  fun buildTokenPattern(wordCharacters: String, exceptionsSplitSentences: String?): Regex {
    val charClass = wordCharacters.ifBlank { defaultWordCharacters() }
    val exceptions = exceptionsSplitSentences?.trim() ?: ""
    val cacheKey = "$charClass|$exceptions"

    return tokenPatternCache.getOrPut(cacheKey) {
      if (exceptions.isNotEmpty()) {
        val escapedExceptions = exceptions.replace(".", "\\.")
        Regex("($escapedExceptions|[$charClass]+)")
      } else {
        Regex("([$charClass]+)")
      }
    }
  }

  fun defaultWordCharacters(): String = "\\p{L}\\p{M}\\p{Sk}'"
}
