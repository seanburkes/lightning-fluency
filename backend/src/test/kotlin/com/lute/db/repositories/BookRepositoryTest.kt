package com.lute.db.repositories

import com.lute.db.DatabaseTestBase
import com.lute.domain.Book
import com.lute.domain.Language
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BookRepositoryTest : DatabaseTestBase() {
  private val langRepo = LanguageRepository()
  private val repo = BookRepository()
  private var langId: Int = 0

  @BeforeEach
  fun seedLanguage() {
    langId = langRepo.save(Language(name = "English"))
  }

  @Test
  fun `save returns generated id`() {
    val id = repo.save(Book(languageId = langId, title = "My Book"))
    assertTrue(id > 0)
  }

  @Test
  fun `findById returns saved book`() {
    val id =
        repo.save(Book(languageId = langId, title = "Test Book", sourceURI = "http://example.com"))
    val found = repo.findById(id)
    assertNotNull(found)
    assertEquals("Test Book", found.title)
    assertEquals("http://example.com", found.sourceURI)
    assertEquals(false, found.archived)
  }

  @Test
  fun `findById returns null for missing id`() {
    assertNull(repo.findById(999))
  }

  @Test
  fun `findAll returns all books`() {
    repo.save(Book(languageId = langId, title = "Book A"))
    repo.save(Book(languageId = langId, title = "Book B"))
    assertEquals(2, repo.findAll().size)
  }

  @Test
  fun `findAll filters by languageId`() {
    val lang2 = langRepo.save(Language(name = "Spanish"))
    repo.save(Book(languageId = langId, title = "English Book"))
    repo.save(Book(languageId = lang2, title = "Spanish Book"))
    assertEquals(1, repo.findAll(languageId = langId).size)
    assertEquals(1, repo.findAll(languageId = lang2).size)
  }

  @Test
  fun `findAll filters by archived`() {
    repo.save(Book(languageId = langId, title = "Active"))
    repo.save(Book(languageId = langId, title = "Archived", archived = true))
    assertEquals(1, repo.findAll(archived = false).size)
    assertEquals(1, repo.findAll(archived = true).size)
  }

  @Test
  fun `update modifies existing book`() {
    val id = repo.save(Book(languageId = langId, title = "Original"))
    val book = repo.findById(id)!!
    repo.update(book.copy(title = "Updated", archived = true))
    val updated = repo.findById(id)!!
    assertEquals("Updated", updated.title)
    assertTrue(updated.archived)
  }

  @Test
  fun `updateCurrentPage sets current text id`() {
    val id = repo.save(Book(languageId = langId, title = "Reading"))
    repo.updateCurrentPage(id, 42)
    assertEquals(42, repo.findById(id)!!.currentTextId)
  }

  @Test
  fun `delete removes book`() {
    val id = repo.save(Book(languageId = langId, title = "To Delete"))
    repo.delete(id)
    assertNull(repo.findById(id))
  }

  @Test
  fun `save preserves all fields`() {
    val book =
        Book(
            languageId = langId,
            title = "Full Book",
            sourceURI = "http://example.com",
            archived = true,
            currentTextId = 5,
            audioFilename = "audio.mp3",
            audioCurrentPos = 12.5f,
            audioBookmarks = "0:10,1:20",
        )
    val id = repo.save(book)
    val found = repo.findById(id)!!
    assertEquals("Full Book", found.title)
    assertEquals("http://example.com", found.sourceURI)
    assertTrue(found.archived)
    assertEquals(5, found.currentTextId)
    assertEquals("audio.mp3", found.audioFilename)
    assertEquals(12.5f, found.audioCurrentPos)
    assertEquals("0:10,1:20", found.audioBookmarks)
  }
}
