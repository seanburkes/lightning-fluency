package com.lute.parse

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SentenceSplitterTest {
  @Test
  fun `containsSentenceEnd detects period`() {
    assertTrue(SentenceSplitter.containsSentenceEnd(".", null))
  }

  @Test
  fun `containsSentenceEnd detects exclamation`() {
    assertTrue(SentenceSplitter.containsSentenceEnd("!", null))
  }

  @Test
  fun `containsSentenceEnd detects question mark`() {
    assertTrue(SentenceSplitter.containsSentenceEnd("?", null))
  }

  @Test
  fun `containsSentenceEnd detects colon`() {
    assertTrue(SentenceSplitter.containsSentenceEnd(":", null))
  }

  @Test
  fun `containsSentenceEnd returns false for space`() {
    assertFalse(SentenceSplitter.containsSentenceEnd(" ", null))
  }

  @Test
  fun `containsSentenceEnd returns false for comma`() {
    assertFalse(SentenceSplitter.containsSentenceEnd(",", null))
  }

  @Test
  fun `containsSentenceEnd uses custom regex`() {
    assertTrue(SentenceSplitter.containsSentenceEnd(";", ";"))
    assertFalse(SentenceSplitter.containsSentenceEnd(".", ";"))
  }

  @Test
  fun `containsSentenceEnd falls back to default for blank regex`() {
    assertTrue(SentenceSplitter.containsSentenceEnd(".", ""))
    assertTrue(SentenceSplitter.containsSentenceEnd(".", "  "))
  }

  @Test
  fun `buildTokenPattern matches word characters`() {
    val pattern = SentenceSplitter.buildTokenPattern("a-z", null)
    val matches = pattern.findAll("hello world").map { it.value }.toList()
    assertEquals(listOf("hello", "world"), matches)
  }

  @Test
  fun `buildTokenPattern with exceptions`() {
    val pattern = SentenceSplitter.buildTokenPattern("a-zA-Z", "Mr.|Dr.")
    val matches = pattern.findAll("Mr. Smith saw Dr. Jones").map { it.value }.toList()
    assertTrue(matches.contains("Mr."))
    assertTrue(matches.contains("Dr."))
    assertTrue(matches.contains("Smith"))
    assertTrue(matches.contains("Jones"))
  }

  @Test
  fun `buildTokenPattern uses default word chars for blank input`() {
    val pattern = SentenceSplitter.buildTokenPattern("", null)
    val matches = pattern.findAll("hello world").map { it.value }.toList()
    assertEquals(listOf("hello", "world"), matches)
  }

  @Test
  fun `defaultWordCharacters matches letters`() {
    val pattern = Regex("[${SentenceSplitter.defaultWordCharacters()}]+")
    assertTrue(pattern.containsMatchIn("hello"))
    assertTrue(pattern.containsMatchIn("café"))
    assertTrue(pattern.containsMatchIn("über"))
  }

  @Test
  fun `defaultWordCharacters matches apostrophe`() {
    val pattern = Regex("[${SentenceSplitter.defaultWordCharacters()}]+")
    val match = pattern.find("don't")
    assertEquals("don't", match?.value)
  }
}
