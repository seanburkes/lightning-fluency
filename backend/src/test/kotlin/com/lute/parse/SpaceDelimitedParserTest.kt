package com.lute.parse

import com.lute.domain.Language
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SpaceDelimitedParserTest {
  private val parser = SpaceDelimitedParser()
  private val english = Language(id = 1, name = "English", parserType = "spacedel")

  @Test
  fun `name is spacedel`() {
    assertEquals("spacedel", parser.name)
  }

  @Test
  fun `getReading returns null`() {
    assertNull(parser.getReading("hello"))
  }

  @Test
  fun `getLowercase returns lowercase`() {
    assertEquals("hello world", parser.getLowercase("Hello World"))
  }

  @Test
  fun `parse simple sentence`() {
    val tokens = parser.parse("Hello world.", english)

    val words = tokens.filter { it.isWord }
    assertEquals(listOf("Hello", "world"), words.map { it.token })
  }

  @Test
  fun `parse marks sentence endings`() {
    val tokens = parser.parse("Hello. World.", english)

    val eosTokens = tokens.filter { it.isEndOfSentence }
    assertTrue(eosTokens.isNotEmpty())
  }

  @Test
  fun `parse assigns sequential order`() {
    val tokens = parser.parse("Hello world.", english)

    val orders = tokens.map { it.order }
    assertEquals(orders.sorted(), orders)
  }

  @Test
  fun `parse tracks sentence numbers`() {
    val tokens = parser.parse("Hello. World.", english)

    val firstSentenceWords = tokens.filter { it.isWord && it.sentenceNumber == 0 }
    val secondSentenceWords = tokens.filter { it.isWord && it.sentenceNumber == 1 }
    assertEquals(listOf("Hello"), firstSentenceWords.map { it.token })
    assertEquals(listOf("World"), secondSentenceWords.map { it.token })
  }

  @Test
  fun `parse handles paragraphs with pilcrow`() {
    val tokens = parser.parse("Hello\nWorld", english)

    val pilcrow = tokens.find { it.token == "\u00B6" }
    assertTrue(pilcrow != null)
    assertTrue(pilcrow.isEndOfSentence)
    assertFalse(pilcrow.isWord)
  }

  @Test
  fun `parse handles multiple spaces`() {
    val tokens = parser.parse("Hello   world", english)

    val words = tokens.filter { it.isWord }
    assertEquals(listOf("Hello", "world"), words.map { it.token })
  }

  @Test
  fun `parse removes zero-width space`() {
    val tokens = parser.parse("Hello\u200Bworld", english)

    val words = tokens.filter { it.isWord }
    assertEquals(listOf("Helloworld"), words.map { it.token })
  }

  @Test
  fun `parse applies character substitutions`() {
    val lang = english.copy(characterSubstitutions = "Hello=Goodbye")
    val tokens = parser.parse("Hello world", lang)

    val words = tokens.filter { it.isWord }
    assertEquals(listOf("Goodbye", "world"), words.map { it.token })
  }

  @Test
  fun `parse with custom word characters`() {
    val lang = english.copy(regexpWordCharacters = "a-z")
    val tokens = parser.parse("hello WORLD", lang)

    val words = tokens.filter { it.isWord }
    assertEquals(listOf("hello"), words.map { it.token })
  }

  @Test
  fun `parse with custom split sentences regex`() {
    val lang = english.copy(regexpSplitSentences = "!")
    val tokens = parser.parse("Hello. World! Done.", lang)

    val eosAfterWorld = tokens.find { !it.isWord && it.token.contains("!") }
    assertTrue(eosAfterWorld != null)
    assertTrue(eosAfterWorld.isEndOfSentence)

    val periodAfterHello = tokens.find { !it.isWord && it.token == ". " }
    if (periodAfterHello != null) {
      assertFalse(periodAfterHello.isEndOfSentence)
    }
  }

  @Test
  fun `parse with exception patterns`() {
    val lang = english.copy(exceptionsSplitSentences = "Mr.|Dr.")
    val tokens = parser.parse("Mr. Smith is here.", lang)

    val words = tokens.filter { it.isWord }
    assertTrue(words.any { it.token == "Mr." })
    assertTrue(words.any { it.token == "Smith" })
  }

  @Test
  fun `parse empty string`() {
    val tokens = parser.parse("", english)
    assertTrue(tokens.isEmpty())
  }

  @Test
  fun `parse handles curly braces replacement`() {
    val tokens = parser.parse("{Hello} world", english)

    val fullText = tokens.joinToString("") { it.token }
    assertTrue(fullText.contains("[Hello]"))
  }
}
