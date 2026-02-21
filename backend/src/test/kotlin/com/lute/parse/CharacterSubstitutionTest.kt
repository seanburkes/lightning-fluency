package com.lute.parse

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CharacterSubstitutionTest {
  @Test
  fun `apply returns text unchanged for null config`() {
    assertEquals("hello", CharacterSubstitution.apply("hello", null))
  }

  @Test
  fun `apply returns text unchanged for blank config`() {
    assertEquals("hello", CharacterSubstitution.apply("hello", ""))
    assertEquals("hello", CharacterSubstitution.apply("hello", "  "))
  }

  @Test
  fun `apply single substitution`() {
    assertEquals("goodbye world", CharacterSubstitution.apply("hello world", "hello=goodbye"))
  }

  @Test
  fun `apply multiple substitutions`() {
    val result = CharacterSubstitution.apply("a b c", "a=x|c=z")
    assertEquals("x b z", result)
  }

  @Test
  fun `apply substitution with empty replacement`() {
    assertEquals("hllo", CharacterSubstitution.apply("hello", "e="))
  }

  @Test
  fun `parse returns empty list for blank config`() {
    assertTrue(CharacterSubstitution.parse("").isEmpty())
    assertTrue(CharacterSubstitution.parse("  ").isEmpty())
  }

  @Test
  fun `parse single pair`() {
    val pairs = CharacterSubstitution.parse("a=b")
    assertEquals(1, pairs.size)
    assertEquals(Pair("a", "b"), pairs[0])
  }

  @Test
  fun `parse multiple pairs`() {
    val pairs = CharacterSubstitution.parse("a=b|c=d")
    assertEquals(2, pairs.size)
    assertEquals(Pair("a", "b"), pairs[0])
    assertEquals(Pair("c", "d"), pairs[1])
  }

  @Test
  fun `parse skips entries without equals sign`() {
    val pairs = CharacterSubstitution.parse("a=b|invalid|c=d")
    assertEquals(2, pairs.size)
    assertEquals(Pair("a", "b"), pairs[0])
    assertEquals(Pair("c", "d"), pairs[1])
  }

  @Test
  fun `parse handles equals in value`() {
    val pairs = CharacterSubstitution.parse("a=b=c")
    assertEquals(1, pairs.size)
    assertEquals(Pair("a", "b=c"), pairs[0])
  }
}
