package com.lute.db.repositories

import com.lute.db.DatabaseTestBase
import com.lute.domain.Language
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class LanguageRepositoryTest : DatabaseTestBase() {
  private val repo = LanguageRepository()

  @Test
  fun `save returns generated id`() {
    val lang = Language(name = "English")
    val id = repo.save(lang)
    assertTrue(id > 0)
  }

  @Test
  fun `findById returns saved language`() {
    val id = repo.save(Language(name = "Spanish", parserType = "spacedel"))
    val found = repo.findById(id)
    assertNotNull(found)
    assertEquals("Spanish", found.name)
    assertEquals("spacedel", found.parserType)
  }

  @Test
  fun `findById returns null for missing id`() {
    assertNull(repo.findById(999))
  }

  @Test
  fun `findByName returns matching language`() {
    repo.save(Language(name = "French"))
    val found = repo.findByName("French")
    assertNotNull(found)
    assertEquals("French", found.name)
  }

  @Test
  fun `findByName returns null when not found`() {
    assertNull(repo.findByName("Nonexistent"))
  }

  @Test
  fun `findAll returns all languages`() {
    repo.save(Language(name = "English"))
    repo.save(Language(name = "Japanese", parserType = "mecab"))
    val all = repo.findAll()
    assertEquals(2, all.size)
  }

  @Test
  fun `update modifies existing language`() {
    val id = repo.save(Language(name = "German"))
    val lang = repo.findById(id)!!
    repo.update(lang.copy(name = "Deutsch", rightToLeft = true))
    val updated = repo.findById(id)!!
    assertEquals("Deutsch", updated.name)
    assertTrue(updated.rightToLeft)
  }

  @Test
  fun `delete removes language`() {
    val id = repo.save(Language(name = "Italian"))
    repo.delete(id)
    assertNull(repo.findById(id))
  }

  @Test
  fun `save preserves all fields`() {
    val lang =
        Language(
            name = "Arabic",
            characterSubstitutions = "a=b",
            regexpSplitSentences = "[.!?]",
            exceptionsSplitSentences = "Mr.|Dr.",
            regexpWordCharacters = "[\\w]",
            rightToLeft = true,
            showRomanization = true,
            parserType = "spacedel",
        )
    val id = repo.save(lang)
    val found = repo.findById(id)!!
    assertEquals("Arabic", found.name)
    assertEquals("a=b", found.characterSubstitutions)
    assertEquals("[.!?]", found.regexpSplitSentences)
    assertEquals("Mr.|Dr.", found.exceptionsSplitSentences)
    assertEquals("[\\w]", found.regexpWordCharacters)
    assertTrue(found.rightToLeft)
    assertTrue(found.showRomanization)
  }
}
