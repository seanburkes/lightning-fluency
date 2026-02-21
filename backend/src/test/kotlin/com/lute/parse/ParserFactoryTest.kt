package com.lute.parse

import com.lute.domain.Language
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ParserFactoryTest {
  private val factory = ParserFactory()

  @Test
  fun `getParser returns SpaceDelimitedParser for spacedel`() {
    val parser = factory.getParser("spacedel")
    assertTrue(parser is SpaceDelimitedParser)
  }

  @Test
  fun `getParser returns TurkishParser for turkish`() {
    val parser = factory.getParser("turkish")
    assertTrue(parser is TurkishParser)
  }

  @Test
  fun `getParser throws for unknown type`() {
    assertFailsWith<IllegalArgumentException> { factory.getParser("unknown") }
  }

  @Test
  fun `getParserForLanguage returns correct parser`() {
    val english = Language(id = 1, name = "English", parserType = "spacedel")
    val parser = factory.getParserForLanguage(english)
    assertTrue(parser is SpaceDelimitedParser)
  }

  @Test
  fun `getParserForLanguage returns TurkishParser for Turkish`() {
    val turkish = Language(id = 2, name = "Turkish", parserType = "turkish")
    val parser = factory.getParserForLanguage(turkish)
    assertTrue(parser is TurkishParser)
  }

  @Test
  fun `supportedParserTypes includes spacedel and turkish`() {
    val types = factory.supportedParserTypes()
    assertTrue(types.contains("spacedel"))
    assertTrue(types.contains("turkish"))
  }

  @Test
  fun `getParser returns consistent instances`() {
    val parser1 = factory.getParser("spacedel")
    val parser2 = factory.getParser("spacedel")
    assertEquals(parser1, parser2)
  }
}
