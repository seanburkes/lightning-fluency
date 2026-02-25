package com.lute.parse

import com.lute.domain.Language
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JapaneseParserTest {
  private val parser = JapaneseParser()

  @Test
  fun `name is Japanese`() {
    assertEquals("Japanese", parser.name)
  }

  @Test
  fun `supported language codes includes ja`() {
    assertEquals(setOf("ja"), parser.supportedLanguageCodes())
  }

  @Test
  fun `parse tokenizes Japanese text`() {
    val language = Language(id = 1, name = "Japanese", parserType = "japanese")
    val tokens = parser.parse("日本語を勉強しています", language)

    assertTrue(tokens.isNotEmpty())
    val words = tokens.filter { it.isWord }
    assertTrue(words.isNotEmpty())
  }

  @Test
  fun `parse includes surface form`() {
    val language = Language(id = 1, name = "Japanese", parserType = "japanese")
    val tokens = parser.parse("日本語", language)

    val surfaceForms = tokens.map { it.token }
    assertTrue(surfaceForms.any { it == "日本語" })
  }

  @Test
  fun `parse marks content words correctly`() {
    val language = Language(id = 1, name = "Japanese", parserType = "japanese")
    val tokens = parser.parse("日本語", language)

    val words = tokens.filter { it.isWord }
    assertTrue(words.isNotEmpty())
  }

  @Test
  fun `parse marks punctuation as non-word`() {
    val language = Language(id = 1, name = "Japanese", parserType = "japanese")
    val tokens = parser.parse(" Hello World! ", language)

    val punctuation = tokens.filter { !it.isWord }
    assertTrue(punctuation.any { it.token == " " })
  }

  @Test
  fun `parse detects sentence boundaries`() {
    val language = Language(id = 1, name = "Japanese", parserType = "japanese")
    val tokens = parser.parse("これは文です。これは二つ目の文です。", language)

    val eosTokens = tokens.filter { it.isEndOfSentence }
    assertTrue(eosTokens.isNotEmpty())
  }

  @Test
  fun `getReading returns katakana for kanji`() {
    val reading = parser.getReading("日本語")
    assertEquals("ニホンゴ", reading)
  }

  @Test
  fun `getReading returns null for hiragana text`() {
    val reading = parser.getReading("にほんご")
    assertNull(reading) // Hiragana text returns null
  }

  @Test
  fun `getReading returns null when reading equals text`() {
    val reading = parser.getReading("にほんご")
    assertNull(reading)
  }

  @Test
  fun `getLowercase returns hiragana normalization`() {
    val lowercase = parser.getLowercase("日本語")
    assertEquals("にほんご", lowercase)
  }
}
