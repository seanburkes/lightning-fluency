package com.lute.services

import com.lute.application.ParserServiceImpl
import com.lute.domain.Language
import com.lute.parse.ParserFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ParserServiceImplTest {
  private val parserFactory = ParserFactory()
  private val parserService = ParserServiceImpl(parserFactory)

  private val english = Language(id = 1, name = "English", parserType = "spacedel")
  private val turkish = Language(id = 2, name = "Turkish", parserType = "turkish")

  @Test
  fun `parseText returns tokens for spacedel parser`() {
    val tokens = parserService.parseText("Hello world.", english)

    assertTrue(tokens.isNotEmpty())
    val words = tokens.filter { it.isWord }
    assertEquals(listOf("Hello", "world"), words.map { it.token })
  }

  @Test
  fun `parseText returns tokens for turkish parser`() {
    val tokens = parserService.parseText("Merhaba dünya.", turkish)

    assertTrue(tokens.isNotEmpty())
    val words = tokens.filter { it.isWord }
    assertEquals(listOf("Merhaba", "dünya"), words.map { it.token })
  }

  @Test
  fun `parseText marks sentence endings`() {
    val tokens = parserService.parseText("Hello. World.", english)

    val eosTokens = tokens.filter { it.isEndOfSentence }
    assertTrue(eosTokens.isNotEmpty())
  }

  @Test
  fun `parseText assigns sequential order`() {
    val tokens = parserService.parseText("Hello world.", english)

    val orders = tokens.map { it.order }
    assertEquals(orders.sorted(), orders)
  }

  @Test
  fun `parseText handles empty string`() {
    val tokens = parserService.parseText("", english)
    assertTrue(tokens.isEmpty())
  }

  @Test
  fun `getReading returns null for spacedel parser`() {
    val result = parserService.getReading("Hello", english)
    assertNull(result)
  }

  @Test
  fun `getReading returns null for turkish parser`() {
    val result = parserService.getReading("Merhaba", turkish)
    assertNull(result)
  }

  @Test
  fun `getLowercase returns lowercase for spacedel parser`() {
    val result = parserService.getLowercase("Hello WORLD", english)
    assertEquals("hello world", result)
  }

  @Test
  fun `getLowercase handles Turkish casing`() {
    val result = parserService.getLowercase("İSTANBUL", turkish)
    assertEquals("istanbul", result)
  }

  @Test
  fun `getLowercase handles Turkish dotless I`() {
    val result = parserService.getLowercase("ISIR", turkish)
    assertEquals("ısır", result)
  }

  @Test
  fun `getLowercase preserves non-Turkish text`() {
    val result = parserService.getLowercase("Hello World", english)
    assertEquals("hello world", result)
  }

  @Test
  fun `parseText with custom word characters`() {
    val lang = english.copy(regexpWordCharacters = "a-z")
    val tokens = parserService.parseText("hello WORLD", lang)

    val words = tokens.filter { it.isWord }
    assertEquals(listOf("hello"), words.map { it.token })
  }

  @Test
  fun `parseText with character substitutions`() {
    val lang = english.copy(characterSubstitutions = "Hello=Goodbye")
    val tokens = parserService.parseText("Hello world", lang)

    val words = tokens.filter { it.isWord }
    assertEquals(listOf("Goodbye", "world"), words.map { it.token })
  }
}
