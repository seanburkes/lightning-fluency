package com.lute.application

import com.lute.application.exceptions.EntityNotFoundException
import com.lute.db.repositories.BookRepository
import com.lute.db.repositories.LanguageRepository
import com.lute.db.repositories.TermRepository
import com.lute.db.repositories.TextRepository
import com.lute.domain.Term
import com.lute.dtos.PageDto
import com.lute.dtos.ReadingPageDto
import com.lute.dtos.TokenDto

class ReadingServiceImpl(
    private val bookRepository: BookRepository,
    private val textRepository: TextRepository,
    private val termRepository: TermRepository,
    private val languageRepository: LanguageRepository,
    private val parserService: ParserService,
) : ReadingService {
  override fun getPage(bookId: Long, pageNum: Int): ReadingPageDto? {
    val book = bookRepository.require(bookId, "Book")

    val text = textRepository.findByBookAndOrder(bookId, pageNum) ?: return null

    val language = languageRepository.findById(book.languageId) ?: return null

    val tokens = parsePageWithTerms(text.text, language.id)

    return ReadingPageDto(
        page =
            PageDto(
                id = text.id,
                order = text.order,
                text = text.text,
            ),
        tokens = tokens,
    )
  }

  override fun parsePageWithTerms(text: String, languageId: Long): List<TokenDto> {
    val language = languageRepository.require(languageId, "Language")

    val parsedTokens = parserService.parseText(text, language)
    val termMap = lookupTermsForPage(parsedTokens.map { it.token }, languageId)

    return parsedTokens.map { parsedToken ->
      val term = termMap[parsedToken.token.lowercase()]
      TokenDto(
          token = parsedToken.token,
          is_word = parsedToken.isWord,
          status = term?.status,
          term_id = term?.id,
          translation = term?.translation,
          romanization = term?.romanization,
      )
    }
  }

  override fun getNextPage(bookId: Long, currentPage: Int): Int? {
    val nextPage = currentPage + 1
    val text = textRepository.findByBookAndOrder(bookId, nextPage)
    return if (text != null) nextPage else null
  }

  override fun getPreviousPage(bookId: Long, currentPage: Int): Int? {
    if (currentPage <= 1) return null
    val prevPage = currentPage - 1
    val text = textRepository.findByBookAndOrder(bookId, prevPage)
    return if (text != null) prevPage else null
  }

  override fun saveCurrentPage(bookId: Long, pageNum: Int) {
    bookRepository.require(bookId, "Book")

    val text =
        textRepository.findByBookAndOrder(bookId, pageNum)
            ?: throw EntityNotFoundException("Page", pageNum)

    bookRepository.updateCurrentPage(bookId, text.id)
  }

  override fun getCurrentPage(bookId: Long): Int? {
    val book = bookRepository.require(bookId, "Book")

    if (book.currentTextId == 0L) return null

    return textRepository.findById(book.currentTextId)?.order
  }

  private fun lookupTermsForPage(tokens: List<String>, languageId: Long): Map<String, Term?> {
    return termRepository.findByTextsAndLanguage(tokens, languageId)
  }
}
