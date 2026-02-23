package com.lute.services

import com.lute.application.ParserService
import com.lute.application.PopupServiceImpl
import com.lute.application.TermCrudService
import com.lute.application.exceptions.EntityNotFoundException
import com.lute.db.repositories.BookRepository
import com.lute.db.repositories.LanguageRepository
import com.lute.db.repositories.TermRepository
import com.lute.db.repositories.TextRepository
import com.lute.domain.Book
import com.lute.domain.Language
import com.lute.domain.Term
import com.lute.domain.Text
import com.lute.dtos.TermDto
import com.lute.parse.ParsedToken
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PopupServiceTest {
  private val bookRepository = mockk<BookRepository>(relaxed = true)
  private val textRepository = mockk<TextRepository>(relaxed = true)
  private val termRepository = mockk<TermRepository>(relaxed = true)
  private val languageRepository = mockk<LanguageRepository>(relaxed = true)
  private val parserService = mockk<ParserService>(relaxed = true)
  private val termCrudService = mockk<TermCrudService>(relaxed = true)

  private val popupService =
      PopupServiceImpl(
          bookRepository,
          textRepository,
          termRepository,
          languageRepository,
          parserService,
          termCrudService,
      )

  @Test
  fun `getPopupData throws EntityNotFoundException for non-existent book`() {
    every { bookRepository.findById(999L) } returns null

    assertFailsWith<EntityNotFoundException> { popupService.getPopupData(999L, "hello") }
  }

  @Test
  fun `getPopupData throws EntityNotFoundException for non-existent language`() {
    val book = Book(id = 1L, languageId = 10L, title = "Test Book")
    every { bookRepository.findById(1L) } returns book
    every { languageRepository.findById(10L) } returns null

    assertFailsWith<EntityNotFoundException> { popupService.getPopupData(1L, "hello") }
  }

  @Test
  fun `getPopupData returns term data when term exists`() {
    val book = Book(id = 1L, languageId = 10L, title = "Test Book", currentTextId = 100L)
    val language = Language(id = 10L, name = "English")
    val term =
        Term(
            id = 50L,
            languageId = 10L,
            text = "Hello",
            textLC = "hello",
            status = 1,
            translation = "Greeting",
        )
    val text = Text(id = 100L, bookId = 1L, order = 1, text = "Hello world.")
    val termDto =
        TermDto(
            id = 50L,
            text = "Hello",
            language_id = 10L,
            status = 1,
            translation = "Greeting",
        )

    every { bookRepository.findById(1L) } returns book
    every { languageRepository.findById(10L) } returns language
    every { termRepository.findByTextAndLanguage("hello", 10L) } returns term
    every { textRepository.findById(100L) } returns text
    every { parserService.parseText("Hello world.", language) } returns
        listOf(
            ParsedToken(token = "Hello", isWord = true, sentenceNumber = 1),
            ParsedToken(token = " ", isWord = false, sentenceNumber = 1),
            ParsedToken(token = "world", isWord = true, sentenceNumber = 1),
            ParsedToken(token = ".", isWord = false, sentenceNumber = 1),
        )
    every { termCrudService.getTermById(50L) } returns termDto

    val result = popupService.getPopupData(1L, "Hello")

    assertNotNull(result.term)
    assertEquals(50L, result.term.id)
    assertEquals("Hello", result.term.text)
    assertEquals("Greeting", result.term.translation)
    assertEquals(1, result.term.status)
  }

  @Test
  fun `getPopupData returns null term when term does not exist`() {
    val book = Book(id = 1L, languageId = 10L, title = "Test Book", currentTextId = 100L)
    val language = Language(id = 10L, name = "English")
    val text = Text(id = 100L, bookId = 1L, order = 1, text = "Hello world.")

    every { bookRepository.findById(1L) } returns book
    every { languageRepository.findById(10L) } returns language
    every { termRepository.findByTextAndLanguage("hello", 10L) } returns null
    every { textRepository.findById(100L) } returns text
    every { parserService.parseText("Hello world.", language) } returns
        listOf(
            ParsedToken(token = "Hello", isWord = true, sentenceNumber = 1),
            ParsedToken(token = " ", isWord = false, sentenceNumber = 1),
            ParsedToken(token = "world", isWord = true, sentenceNumber = 1),
            ParsedToken(token = ".", isWord = false, sentenceNumber = 1),
        )

    val result = popupService.getPopupData(1L, "Hello")

    assertNull(result.term)
  }

  @Test
  fun `getPopupData returns sentence context`() {
    val book = Book(id = 1L, languageId = 10L, title = "Test Book", currentTextId = 100L)
    val language = Language(id = 10L, name = "English")
    val text = Text(id = 100L, bookId = 1L, order = 1, text = "Hello world. Goodbye now.")

    every { bookRepository.findById(1L) } returns book
    every { languageRepository.findById(10L) } returns language
    every { termRepository.findByTextAndLanguage("hello", 10L) } returns null
    every { textRepository.findById(100L) } returns text
    every { parserService.parseText("Hello world. Goodbye now.", language) } returns
        listOf(
            ParsedToken(token = "Hello", isWord = true, sentenceNumber = 1),
            ParsedToken(token = " ", isWord = false, sentenceNumber = 1),
            ParsedToken(token = "world", isWord = true, sentenceNumber = 1),
            ParsedToken(token = ".", isWord = false, sentenceNumber = 1),
            ParsedToken(token = " ", isWord = false, sentenceNumber = 2),
            ParsedToken(token = "Goodbye", isWord = true, sentenceNumber = 2),
            ParsedToken(token = " ", isWord = false, sentenceNumber = 2),
            ParsedToken(token = "now", isWord = true, sentenceNumber = 2),
            ParsedToken(token = ".", isWord = false, sentenceNumber = 2),
        )

    val result = popupService.getPopupData(1L, "Hello")

    assertEquals("Hello world.", result.sentence)
  }

  @Test
  fun `getPopupData returns context with surrounding sentences`() {
    val book = Book(id = 1L, languageId = 10L, title = "Test Book", currentTextId = 100L)
    val language = Language(id = 10L, name = "English")
    val text = Text(id = 100L, bookId = 1L, order = 1, text = "First one. Hello world. Last one.")

    every { bookRepository.findById(1L) } returns book
    every { languageRepository.findById(10L) } returns language
    every { termRepository.findByTextAndLanguage("hello", 10L) } returns null
    every { textRepository.findById(100L) } returns text
    every { parserService.parseText("First one. Hello world. Last one.", language) } returns
        listOf(
            ParsedToken(token = "First", isWord = true, sentenceNumber = 1),
            ParsedToken(token = " ", isWord = false, sentenceNumber = 1),
            ParsedToken(token = "one", isWord = true, sentenceNumber = 1),
            ParsedToken(token = ".", isWord = false, sentenceNumber = 1),
            ParsedToken(token = " ", isWord = false, sentenceNumber = 2),
            ParsedToken(token = "Hello", isWord = true, sentenceNumber = 2),
            ParsedToken(token = " ", isWord = false, sentenceNumber = 2),
            ParsedToken(token = "world", isWord = true, sentenceNumber = 2),
            ParsedToken(token = ".", isWord = false, sentenceNumber = 2),
            ParsedToken(token = " ", isWord = false, sentenceNumber = 3),
            ParsedToken(token = "Last", isWord = true, sentenceNumber = 3),
            ParsedToken(token = " ", isWord = false, sentenceNumber = 3),
            ParsedToken(token = "one", isWord = true, sentenceNumber = 3),
            ParsedToken(token = ".", isWord = false, sentenceNumber = 3),
        )

    val result = popupService.getPopupData(1L, "Hello")

    assertEquals("Hello world.", result.sentence)
    assertTrue(result.context!!.contains("First one."))
    assertTrue(result.context.contains("Hello world."))
    assertTrue(result.context.contains("Last one."))
  }

  @Test
  fun `getPopupData returns null sentence when book has no current page`() {
    val book = Book(id = 1L, languageId = 10L, title = "Test Book", currentTextId = 0L)
    val language = Language(id = 10L, name = "English")

    every { bookRepository.findById(1L) } returns book
    every { languageRepository.findById(10L) } returns language
    every { termRepository.findByTextAndLanguage("hello", 10L) } returns null

    val result = popupService.getPopupData(1L, "Hello")

    assertNull(result.sentence)
    assertNull(result.context)
  }

  @Test
  fun `getPopupData returns null sentence when word not found in text`() {
    val book = Book(id = 1L, languageId = 10L, title = "Test Book", currentTextId = 100L)
    val language = Language(id = 10L, name = "English")
    val text = Text(id = 100L, bookId = 1L, order = 1, text = "Different words here.")

    every { bookRepository.findById(1L) } returns book
    every { languageRepository.findById(10L) } returns language
    every { termRepository.findByTextAndLanguage("hello", 10L) } returns null
    every { textRepository.findById(100L) } returns text
    every { parserService.parseText("Different words here.", language) } returns
        listOf(
            ParsedToken(token = "Different", isWord = true, sentenceNumber = 1),
            ParsedToken(token = " ", isWord = false, sentenceNumber = 1),
            ParsedToken(token = "words", isWord = true, sentenceNumber = 1),
            ParsedToken(token = " ", isWord = false, sentenceNumber = 1),
            ParsedToken(token = "here", isWord = true, sentenceNumber = 1),
            ParsedToken(token = ".", isWord = false, sentenceNumber = 1),
        )

    val result = popupService.getPopupData(1L, "Hello")

    assertNull(result.sentence)
    assertNull(result.context)
  }

  @Test
  fun `getPopupData includes term tags from TermCrudService`() {
    val book = Book(id = 1L, languageId = 10L, title = "Test Book", currentTextId = 100L)
    val language = Language(id = 10L, name = "English")
    val term = Term(id = 50L, languageId = 10L, text = "Hello", textLC = "hello", status = 1)
    val text = Text(id = 100L, bookId = 1L, order = 1, text = "Hello.")
    val termDto =
        TermDto(
            id = 50L,
            text = "Hello",
            language_id = 10L,
            status = 1,
            tags = listOf("noun", "common"),
        )

    every { bookRepository.findById(1L) } returns book
    every { languageRepository.findById(10L) } returns language
    every { termRepository.findByTextAndLanguage("hello", 10L) } returns term
    every { textRepository.findById(100L) } returns text
    every { parserService.parseText("Hello.", language) } returns
        listOf(
            ParsedToken(token = "Hello", isWord = true, sentenceNumber = 1),
            ParsedToken(token = ".", isWord = false, sentenceNumber = 1),
        )
    every { termCrudService.getTermById(50L) } returns termDto

    val result = popupService.getPopupData(1L, "Hello")

    assertNotNull(result.term)
    assertEquals(2, result.term.tags.size)
    assertTrue(result.term.tags.contains("noun"))
    assertTrue(result.term.tags.contains("common"))
  }
}
