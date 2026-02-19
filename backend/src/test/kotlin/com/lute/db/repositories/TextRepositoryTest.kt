package com.lute.db.repositories

import com.lute.db.DatabaseTestBase
import com.lute.domain.Book
import com.lute.domain.Language
import com.lute.domain.Text
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TextRepositoryTest : DatabaseTestBase() {
  private val langRepo = LanguageRepository()
  private val bookRepo = BookRepository()
  private val repo = TextRepository()
  private var bookId: Int = 0

  @BeforeEach
  fun seedData() {
    val langId = langRepo.save(Language(name = "English"))
    bookId = bookRepo.save(Book(languageId = langId, title = "Test Book"))
  }

  @Test
  fun `save returns generated id`() {
    val id = repo.save(Text(bookId = bookId, order = 1, text = "Page one."))
    assertTrue(id > 0)
  }

  @Test
  fun `findById returns saved text`() {
    val id = repo.save(Text(bookId = bookId, order = 1, text = "Hello world.", wordCount = 2))
    val found = repo.findById(id)
    assertNotNull(found)
    assertEquals("Hello world.", found.text)
    assertEquals(1, found.order)
    assertEquals(2, found.wordCount)
  }

  @Test
  fun `findById returns null for missing id`() {
    assertNull(repo.findById(999))
  }

  @Test
  fun `findByBookId returns texts ordered by TxOrder`() {
    repo.save(Text(bookId = bookId, order = 3, text = "Third"))
    repo.save(Text(bookId = bookId, order = 1, text = "First"))
    repo.save(Text(bookId = bookId, order = 2, text = "Second"))
    val texts = repo.findByBookId(bookId)
    assertEquals(3, texts.size)
    assertEquals("First", texts[0].text)
    assertEquals("Second", texts[1].text)
    assertEquals("Third", texts[2].text)
  }

  @Test
  fun `findByBookAndOrder returns matching text`() {
    repo.save(Text(bookId = bookId, order = 1, text = "Page 1"))
    repo.save(Text(bookId = bookId, order = 2, text = "Page 2"))
    val found = repo.findByBookAndOrder(bookId, 2)
    assertNotNull(found)
    assertEquals("Page 2", found.text)
  }

  @Test
  fun `findByBookAndOrder returns null when not found`() {
    assertNull(repo.findByBookAndOrder(bookId, 99))
  }

  @Test
  fun `getCountForBook returns correct count`() {
    repo.save(Text(bookId = bookId, order = 1, text = "A"))
    repo.save(Text(bookId = bookId, order = 2, text = "B"))
    assertEquals(2, repo.getCountForBook(bookId))
    assertEquals(0, repo.getCountForBook(999))
  }

  @Test
  fun `update modifies existing text`() {
    val id = repo.save(Text(bookId = bookId, order = 1, text = "Original"))
    val text = repo.findById(id)!!
    repo.update(text.copy(text = "Updated", wordCount = 5))
    val updated = repo.findById(id)!!
    assertEquals("Updated", updated.text)
    assertEquals(5, updated.wordCount)
  }

  @Test
  fun `delete removes text`() {
    val id = repo.save(Text(bookId = bookId, order = 1, text = "To delete"))
    repo.delete(id)
    assertNull(repo.findById(id))
  }
}
