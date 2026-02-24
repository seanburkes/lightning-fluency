package com.lute.application

import com.lute.db.repositories.BookRepository
import com.lute.db.repositories.LanguageRepository
import com.lute.db.repositories.TermRepository
import com.lute.db.repositories.TextRepository
import com.lute.db.repositories.require
import com.lute.domain.Book
import com.lute.domain.Language
import com.lute.domain.Term
import com.lute.dtos.PopupDto

class PopupServiceImpl(
    private val bookRepository: BookRepository,
    private val textRepository: TextRepository,
    private val termRepository: TermRepository,
    private val languageRepository: LanguageRepository,
    private val parserService: ParserService,
    private val termCrudService: TermCrudService,
) : PopupService {
  override fun getPopupData(bookId: Long, word: String): PopupDto {
    val book = bookRepository.require(bookId, "Book")

    val language = languageRepository.require(book.languageId, "Language")

    val term = getTermForWord(word.lowercase(), language.id)

    val termDto = term?.let { termCrudService.getTermById(it.id) }

    val (sentence, context) = getSentenceContext(book, language, word)

    return PopupDto(term = termDto, sentence = sentence, context = context)
  }

  private fun getTermForWord(textLC: String, languageId: Long): Term? {
    return termRepository.findByTextAndLanguage(textLC, languageId)
  }

  private fun getSentenceContext(
      book: Book,
      language: Language,
      word: String,
  ): Pair<String?, String?> {
    val currentTextId = book.currentTextId
    if (currentTextId == 0L) return Pair(null, null)

    val text = textRepository.findById(currentTextId) ?: return Pair(null, null)

    val parsedTokens = parserService.parseText(text.text, language)

    val wordLower = word.lowercase()
    val matchingToken = parsedTokens.find { it.token.lowercase() == wordLower }

    if (matchingToken == null) return Pair(null, null)

    val sentenceNumber = matchingToken.sentenceNumber

    val sentenceTokens = parsedTokens.filter { it.sentenceNumber == sentenceNumber }
    val sentence = sentenceTokens.joinToString("") { it.token }.trim()

    val contextStart = (sentenceNumber - 1).coerceAtLeast(1)
    val contextEnd = sentenceNumber + 1

    val contextTokens = parsedTokens.filter { it.sentenceNumber in contextStart..contextEnd }
    val context = contextTokens.joinToString("") { it.token }.trim()

    return Pair(sentence, context)
  }
}
