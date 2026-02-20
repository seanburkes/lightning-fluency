package com.lute.db.repositories

import com.lute.db.DatabaseTestBase
import com.lute.domain.Book
import com.lute.domain.BookStats
import com.lute.domain.Language
import com.lute.domain.Term
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BookStatsRepositoryTest : DatabaseTestBase() {
  private val langRepo = LanguageRepository()
  private val bookRepo = BookRepository()
  private val termRepo = TermRepository()
  private val repo = BookStatsRepository()
  private var langId: Long = 0
  private var bookId: Long = 0

  @BeforeEach
  fun seedData() {
    langId = langRepo.save(Language(name = "English"))
    bookId = bookRepo.save(Book(languageId = langId, title = "Stats Book"))
  }

  @Test
  fun `findByBookId returns null when no stats exist`() {
    assertNull(repo.findByBookId(bookId))
  }

  @Test
  fun `update inserts new stats`() {
    val stats =
        BookStats(bookId = bookId, distinctTerms = 10, distinctUnknowns = 3, unknownPercent = 30)
    repo.update(stats)
    val found = repo.findByBookId(bookId)
    assertNotNull(found)
    assertEquals(10, found.distinctTerms)
    assertEquals(3, found.distinctUnknowns)
    assertEquals(30, found.unknownPercent)
  }

  @Test
  fun `update overwrites existing stats`() {
    repo.update(BookStats(bookId = bookId, distinctTerms = 5))
    repo.update(BookStats(bookId = bookId, distinctTerms = 15, unknownPercent = 20))
    val found = repo.findByBookId(bookId)!!
    assertEquals(15, found.distinctTerms)
    assertEquals(20, found.unknownPercent)
  }

  @Test
  fun `calculateAndSave computes stats from terms`() {
    termRepo.save(Term(languageId = langId, text = "A", textLC = "a", status = 0))
    termRepo.save(Term(languageId = langId, text = "B", textLC = "b", status = 1))
    termRepo.save(Term(languageId = langId, text = "C", textLC = "c", status = 0))
    repo.calculateAndSave(bookId)
    val stats = repo.findByBookId(bookId)!!
    assertEquals(3, stats.distinctTerms)
    assertEquals(2, stats.distinctUnknowns)
    assertEquals(66, stats.unknownPercent)
  }

  @Test
  fun `calculateAndSave handles no terms`() {
    repo.calculateAndSave(bookId)
    val stats = repo.findByBookId(bookId)!!
    assertEquals(0, stats.distinctTerms)
    assertEquals(0, stats.unknownPercent)
  }
}
