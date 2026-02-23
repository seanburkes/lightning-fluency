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
  private val termCache = mutableMapOf<String, Term?>()

  override fun getPage(bookId: Long, pageNum: Int): ReadingPageDto? {
    val book = bookRepository.findById(bookId) ?: throw EntityNotFoundException("Book", bookId)

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
    val language =
        languageRepository.findById(languageId)
            ?: throw EntityNotFoundException("Language", languageId)

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
    return currentPage - 1
  }

  override fun saveCurrentPage(bookId: Long, pageNum: Int) {
    val book = bookRepository.findById(bookId) ?: throw EntityNotFoundException("Book", bookId)

    val text =
        textRepository.findByBookAndOrder(bookId, pageNum)
            ?: throw EntityNotFoundException("Page", pageNum)

    bookRepository.updateCurrentPage(bookId, text.id)
  }

  override fun getCurrentPage(bookId: Long): Int? {
    val book = bookRepository.findById(bookId) ?: throw EntityNotFoundException("Book", bookId)

    if (book.currentTextId == 0L) return null

    val text = textRepository.findById(book.currentTextId) ?: return null
    return text.order
  }

  private fun lookupTermsForPage(tokens: List<String>, languageId: Long): Map<String, Term?> {
    val result = mutableMapOf<String, Term?>()
    val uncachedTokens = mutableSetOf<String>()

    for (token in tokens) {
      val lowercased = token.lowercase()
      if (termCache.containsKey(lowercased)) {
        result[lowercased] = termCache[lowercased]
      } else {
        uncachedTokens.add(lowercased)
      }
    }

    for (tokenText in uncachedTokens) {
      val term = termRepository.findByTextAndLanguage(tokenText, languageId)
      termCache[tokenText] = term
      result[tokenText] = term
    }

    return result
  }
}
