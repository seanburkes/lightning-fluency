package com.lute.parse

import kotlin.test.Test
import kotlin.test.assertEquals

class TurkishParserTest {
  private val parser = TurkishParser()

  @Test
  fun `name is turkish`() {
    assertEquals("turkish", parser.name)
  }

  @Test
  fun `getReading returns null`() {
    assertEquals(null, parser.getReading("merhaba"))
  }

  @Test
  fun `getLowercase converts dotted I correctly`() {
    assertEquals("istanbul", parser.getLowercase("İstanbul"))
  }

  @Test
  fun `getLowercase converts dotless I correctly`() {
    assertEquals("\u0131stanbul", parser.getLowercase("Istanbul"))
  }

  @Test
  fun `getLowercase handles mixed Turkish characters`() {
    assertEquals("i\u0131", parser.getLowercase("İI"))
  }

  @Test
  fun `getLowercase handles non-Turkish text normally`() {
    assertEquals("hello", parser.getLowercase("HELLO"))
  }

  @Test
  fun `getLowercase handles empty string`() {
    assertEquals("", parser.getLowercase(""))
  }

  @Test
  fun `parse delegates to space-delimited logic`() {
    val language = com.lute.domain.Language(id = 1, name = "Turkish", parserType = "turkish")
    val tokens = parser.parse("Merhaba dünya.", language)

    val words = tokens.filter { it.isWord }
    assertEquals(listOf("Merhaba", "dünya"), words.map { it.token })
  }
}
