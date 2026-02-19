package com.lute.db.repositories

import com.lute.db.DatabaseTestBase
import com.lute.domain.Language
import com.lute.domain.Term
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TermRepositoryTest : DatabaseTestBase() {
  private val langRepo = LanguageRepository()
  private val repo = TermRepository()
  private var langId: Int = 0

  @BeforeEach
  fun seedLanguage() {
    langId = langRepo.save(Language(name = "English"))
  }

  @Test
  fun `save returns generated id`() {
    val id = repo.save(Term(languageId = langId, text = "Hello", textLC = "hello"))
    assertTrue(id > 0)
  }

  @Test
  fun `save auto-populates textLC when empty`() {
    val id = repo.save(Term(languageId = langId, text = "Hello"))
    val found = repo.findById(id)!!
    assertEquals("hello", found.textLC)
  }

  @Test
  fun `findById returns saved term`() {
    val term =
        Term(languageId = langId, text = "Book", textLC = "book", translation = "libro", status = 3)
    val id = repo.save(term)
    val found = repo.findById(id)
    assertNotNull(found)
    assertEquals("Book", found.text)
    assertEquals("book", found.textLC)
    assertEquals("libro", found.translation)
    assertEquals(3, found.status)
  }

  @Test
  fun `findById returns null for missing id`() {
    assertNull(repo.findById(999))
  }

  @Test
  fun `findByTextAndLanguage returns matching term`() {
    repo.save(Term(languageId = langId, text = "Cat", textLC = "cat"))
    val found = repo.findByTextAndLanguage("cat", langId)
    assertNotNull(found)
    assertEquals("Cat", found.text)
  }

  @Test
  fun `findByTextAndLanguage returns null when not found`() {
    assertNull(repo.findByTextAndLanguage("nonexistent", langId))
  }

  @Test
  fun `findAll returns terms with optional filters`() {
    repo.save(Term(languageId = langId, text = "A", textLC = "a", status = 1))
    repo.save(Term(languageId = langId, text = "B", textLC = "b", status = 2))
    repo.save(Term(languageId = langId, text = "C", textLC = "c", status = 1))

    assertEquals(3, repo.findAll().size)
    assertEquals(2, repo.findAll(status = 1).size)
    assertEquals(1, repo.findAll(status = 2).size)
    assertEquals(3, repo.findAll(languageId = langId).size)
  }

  @Test
  fun `findAll supports limit and offset`() {
    repo.save(Term(languageId = langId, text = "A", textLC = "a"))
    repo.save(Term(languageId = langId, text = "B", textLC = "b"))
    repo.save(Term(languageId = langId, text = "C", textLC = "c"))

    assertEquals(2, repo.findAll(limit = 2).size)
    assertEquals(1, repo.findAll(limit = 2, offset = 2).size)
  }

  @Test
  fun `update modifies existing term`() {
    val id = repo.save(Term(languageId = langId, text = "Dog", textLC = "dog"))
    val term = repo.findById(id)!!
    repo.update(term.copy(translation = "perro", status = 5))
    val updated = repo.findById(id)!!
    assertEquals("perro", updated.translation)
    assertEquals(5, updated.status)
  }

  @Test
  fun `delete removes term`() {
    val id = repo.save(Term(languageId = langId, text = "Fish", textLC = "fish"))
    repo.delete(id)
    assertNull(repo.findById(id))
  }

  @Test
  fun `countByLanguage returns correct count`() {
    repo.save(Term(languageId = langId, text = "X", textLC = "x"))
    repo.save(Term(languageId = langId, text = "Y", textLC = "y"))
    assertEquals(2, repo.countByLanguage(langId))
    assertEquals(0, repo.countByLanguage(999))
  }

  @Test
  fun `save preserves all fields`() {
    val term =
        Term(
            languageId = langId,
            text = "Word",
            textLC = "word",
            status = 3,
            translation = "translation",
            romanization = "romanized",
            tokenCount = 2,
            syncStatus = 1,
        )
    val id = repo.save(term)
    val found = repo.findById(id)!!
    assertEquals("Word", found.text)
    assertEquals("word", found.textLC)
    assertEquals(3, found.status)
    assertEquals("translation", found.translation)
    assertEquals("romanized", found.romanization)
    assertEquals(2, found.tokenCount)
    assertEquals(1, found.syncStatus)
    assertNotNull(found.created)
    assertNotNull(found.statusChanged)
  }
}
