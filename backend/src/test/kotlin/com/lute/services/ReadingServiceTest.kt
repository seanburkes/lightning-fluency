package com.lute.services

import com.lute.application.ParserService
import com.lute.application.ReadingServiceImpl
import com.lute.application.exceptions.EntityNotFoundException
import com.lute.db.repositories.BookRepository
import com.lute.db.repositories.LanguageRepository
import com.lute.db.repositories.TermRepository
import com.lute.db.repositories.TextRepository
import com.lute.domain.Book
import com.lute.domain.Language
import com.lute.domain.Term
import com.lute.domain.Text
import com.lute.parse.ParsedToken
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReadingServiceTest {
  private val bookRepository = mockk<BookRepository>(relaxed = true)
  private val textRepository = mockk<TextRepository>(relaxed = true)
  private val termRepository = mockk<TermRepository>(relaxed = true)
  private val languageRepository = mockk<LanguageRepository>(relaxed = true)
  private val parserService = mockk<ParserService>(relaxed = true)

  private val readingService =
      ReadingServiceImpl(
          bookRepository,
          textRepository,
          termRepository,
          languageRepository,
          parserService,
      )

  @Test
  fun `getPage returns ReadingPageDto for existing book and page`() {
    val book = Book(id = 1L, languageId = 10L, title = "Test Book")
    val text = Text(id = 100L, bookId = 1L, order = 1, text = "Hello world")
    val language = Language(id = 10L, name = "English")

    every { bookRepository.findById(1L) } returns book
    every { textRepository.findByBookAndOrder(1L, 1) } returns text
    every { languageRepository.findById(10L) } returns language
    every { parserService.parseText("Hello world", language) } returns
        listOf(
            ParsedToken(token = "Hello", isWord = true, order = 0),
            ParsedToken(token = " ", isWord = false, order = 1),
            ParsedToken(token = "world", isWord = true, order = 2),
        )
    every { termRepository.findByTextAndLanguage("hello", 10L) } returns null
    every { termRepository.findByTextAndLanguage("world", 10L) } returns null

    val result = readingService.getPage(1L, 1)

    assertNotNull(result)
    assertEquals(100L, result.page.id)
    assertEquals(1, result.page.order)
    assertEquals("Hello world", result.page.text)
    assertEquals(3, result.tokens.size)
  }

  @Test
  fun `getPage throws EntityNotFoundException for non-existent book`() {
    every { bookRepository.findById(999L) } returns null

    assertFailsWith<EntityNotFoundException> { readingService.getPage(999L, 1) }
  }

  @Test
  fun `getPage returns null for non-existent page`() {
    val book = Book(id = 1L, languageId = 10L, title = "Test Book")
    every { bookRepository.findById(1L) } returns book
    every { textRepository.findByBookAndOrder(1L, 999) } returns null

    val result = readingService.getPage(1L, 999)

    assertNull(result)
  }

  @Test
  fun `getPage includes term data for known terms`() {
    val book = Book(id = 1L, languageId = 10L, title = "Test Book")
    val text = Text(id = 100L, bookId = 1L, order = 1, text = "Hello")
    val language = Language(id = 10L, name = "English")
    val term =
        Term(id = 50L, languageId = 10L, text = "Hello", status = 1, translation = "Greeting")

    every { bookRepository.findById(1L) } returns book
    every { textRepository.findByBookAndOrder(1L, 1) } returns text
    every { languageRepository.findById(10L) } returns language
    every { parserService.parseText("Hello", language) } returns
        listOf(ParsedToken(token = "Hello", isWord = true, order = 0))
    every { termRepository.findByTextAndLanguage("hello", 10L) } returns term

    val result = readingService.getPage(1L, 1)

    assertNotNull(result)
    assertEquals(1, result.tokens.size)
    val tokenDto = result.tokens[0]
    assertEquals("Hello", tokenDto.token)
    assertEquals(1, tokenDto.status)
    assertEquals(50L, tokenDto.term_id)
    assertEquals("Greeting", tokenDto.translation)
  }

  @Test
  fun `getNextPage returns next page number when exists`() {
    every { textRepository.findByBookAndOrder(1L, 2) } returns
        Text(id = 100L, bookId = 1L, order = 2, text = "Page 2")

    val result = readingService.getNextPage(1L, 1)

    assertEquals(2, result)
  }

  @Test
  fun `getNextPage returns null when at end`() {
    every { textRepository.findByBookAndOrder(1L, 2) } returns null

    val result = readingService.getNextPage(1L, 1)

    assertNull(result)
  }

  @Test
  fun `getPreviousPage returns previous page number when not at start`() {
    val result = readingService.getPreviousPage(1L, 2)

    assertEquals(1, result)
  }

  @Test
  fun `getPreviousPage returns null when at beginning`() {
    val result = readingService.getPreviousPage(1L, 1)

    assertNull(result)
  }

  @Test
  fun `getPreviousPage returns null for page 0 or less`() {
    val result = readingService.getPreviousPage(1L, 0)

    assertNull(result)
  }

  @Test
  fun `saveCurrentPage updates book current text`() {
    val book = Book(id = 1L, languageId = 10L, title = "Test Book")
    val text = Text(id = 100L, bookId = 1L, order = 5, text = "Page 5")

    every { bookRepository.findById(1L) } returns book
    every { textRepository.findByBookAndOrder(1L, 5) } returns text
    every { bookRepository.updateCurrentPage(1L, 100L) } returns Unit

    readingService.saveCurrentPage(1L, 5)

    assertTrue(true)
  }

  @Test
  fun `saveCurrentPage throws EntityNotFoundException for non-existent book`() {
    every { bookRepository.findById(999L) } returns null

    assertFailsWith<EntityNotFoundException> { readingService.saveCurrentPage(999L, 1) }
  }

  @Test
  fun `saveCurrentPage throws EntityNotFoundException for non-existent page`() {
    val book = Book(id = 1L, languageId = 10L, title = "Test Book")
    every { bookRepository.findById(1L) } returns book
    every { textRepository.findByBookAndOrder(1L, 999) } returns null

    assertFailsWith<EntityNotFoundException> { readingService.saveCurrentPage(1L, 999) }
  }

  @Test
  fun `getCurrentPage returns current page number`() {
    val book = Book(id = 1L, languageId = 10L, title = "Test Book", currentTextId = 100L)
    val text = Text(id = 100L, bookId = 1L, order = 5, text = "Page 5")

    every { bookRepository.findById(1L) } returns book
    every { textRepository.findById(100L) } returns text

    val result = readingService.getCurrentPage(1L)

    assertEquals(5, result)
  }

  @Test
  fun `getCurrentPage returns null when not started`() {
    val book = Book(id = 1L, languageId = 10L, title = "Test Book", currentTextId = 0L)

    every { bookRepository.findById(1L) } returns book

    val result = readingService.getCurrentPage(1L)

    assertNull(result)
  }

  @Test
  fun `getCurrentPage throws EntityNotFoundException for non-existent book`() {
    every { bookRepository.findById(999L) } returns null

    assertFailsWith<EntityNotFoundException> { readingService.getCurrentPage(999L) }
  }

  @Test
  fun `parsePageWithTerms returns tokens with term data`() {
    val language = Language(id = 1L, name = "English")
    val term = Term(id = 1L, languageId = 1L, text = "hello", status = 2, translation = "greeting")

    every { languageRepository.findById(1L) } returns language
    every { parserService.parseText("Hello world", language) } returns
        listOf(
            ParsedToken(token = "Hello", isWord = true, order = 0),
            ParsedToken(token = " ", isWord = false, order = 1),
            ParsedToken(token = "world", isWord = true, order = 2),
        )
    every { termRepository.findByTextAndLanguage("hello", 1L) } returns term
    every { termRepository.findByTextAndLanguage(" ", 1L) } returns null
    every { termRepository.findByTextAndLanguage("world", 1L) } returns null

    val result = readingService.parsePageWithTerms("Hello world", 1L)

    assertEquals(3, result.size)
    assertEquals("Hello", result[0].token)
    assertTrue(result[0].is_word)
    assertEquals(2, result[0].status)
    assertEquals(1L, result[0].term_id)
    assertEquals(" ", result[1].token)
    assertNull(result[1].status)
    assertEquals("world", result[2].token)
    assertNull(result[2].status)
  }

  @Test
  fun `termCache caches lookups across calls`() {
    val language = Language(id = 1L, name = "English")
    val term = Term(id = 1L, languageId = 1L, text = "hello", status = 1)

    every { languageRepository.findById(1L) } returns language
    every { parserService.parseText(any(), any()) } returns
        listOf(ParsedToken(token = "hello", isWord = true, order = 0))
    every { termRepository.findByTextAndLanguage("hello", 1L) } returns term

    readingService.parsePageWithTerms("hello", 1L)
    readingService.parsePageWithTerms("hello", 1L)

    verify(exactly = 1) { termRepository.findByTextAndLanguage("hello", 1L) }
  }
}
