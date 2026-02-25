package com.lute.parse

import com.atilika.kuromoji.ipadic.Token
import com.atilika.kuromoji.ipadic.Tokenizer
import com.lute.domain.Language

class JapaneseParser : Parser {
  private val tokenizer = Tokenizer.Builder().build()

  override val name: String = "Japanese"

  fun supportedLanguageCodes(): Set<String> = setOf("ja")

  override fun parse(text: String, language: Language): List<ParsedToken> {
    val tokens = tokenizer.tokenize(text)
    val result = mutableListOf<ParsedToken>()
    var order = 0
    var sentenceNumber = 0

    for (token in tokens) {
      val isWord = isContentWord(token)
      val isEndOfSentence = token.surface == "。" || token.surface == "！" || token.surface == "？"

      result.add(
          ParsedToken(
              token = token.surface,
              isWord = isWord,
              isEndOfSentence = isEndOfSentence,
              order = order++,
              sentenceNumber = sentenceNumber,
          ),
      )
      if (isEndOfSentence) {
        sentenceNumber++
      }
    }

    return result
  }

  override fun getReading(text: String): String? {
    val tokens = tokenizer.tokenize(text)

    // If text is entirely hiragana, return null (reading not needed)
    if (isAllHiragana(text)) {
      return null
    }

    val readings = mutableListOf<String>()
    for (token in tokens) {
      val reading = token.reading
      if (reading != null && reading.isNotBlank() && !reading.contains("�")) {
        // Convert to the appropriate format
        val normalizedReading = normalizeReading(reading)
        if (normalizedReading.isNotBlank()) {
          readings.add(normalizedReading)
        }
      }
    }

    if (readings.isEmpty()) {
      return null
    }

    val result = readings.joinToString("")
    // Return null if reading is the same as input (no value added)
    return if (result == text) null else result
  }

  override fun getLowercase(text: String): String {
    // For Japanese, lowercase means normalizing to hiragana
    val tokens = tokenizer.tokenize(text)
    val result = StringBuilder()

    for (token in tokens) {
      val reading = token.reading
      if (reading != null && reading.isNotBlank()) {
        result.append(toHiragana(reading))
      } else {
        result.append(token.surface)
      }
    }

    return result.toString()
  }

  private fun isContentWord(token: Token): Boolean {
    val pos = token.partOfSpeechLevel1
    // Content words: nouns, verbs, adjectives, adverbs
    return pos.startsWith("名詞") ||
        pos.startsWith("動詞") ||
        pos.startsWith("形容詞") ||
        pos.startsWith("形容動詞") ||
        pos.startsWith("副詞")
  }

  private fun isAllHiragana(text: String): Boolean {
    return text.all { c -> !isJapanese(c) || isHiragana(c) }
  }

  private fun isJapanese(c: Char): Boolean {
    val code = c.code
    return (code in 0x3040..0x309F) || // Hiragana
        (code in 0x30A0..0x30FF) || // Katakana
        (code in 0x4E00..0x9FFF) // Kanji
  }

  private fun isHiragana(c: Char): Boolean {
    return c.code in 0x3040..0x309F
  }

  private fun normalizeReading(reading: String): String {
    // Kuromoji returns katakana readings; return as-is
    return reading
  }

  private fun toHiragana(katakana: String): String {
    val result = StringBuilder()
    for (c in katakana) {
      val code = c.code
      // Convert katakana to hiragana (0x30xx -> 0x30xx - 0x60)
      if (code in 0x30A0..0x30FF) {
        result.append((code - 0x60).toChar())
      } else {
        result.append(c)
      }
    }
    return result.toString()
  }
}
