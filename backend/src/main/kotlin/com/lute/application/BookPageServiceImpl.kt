package com.lute.application

import com.lute.application.exceptions.EntityNotFoundException
import com.lute.db.repositories.BookRepository
import com.lute.db.repositories.TextRepository
import com.lute.domain.Text
import com.lute.dtos.TextDto
import com.lute.utils.DateFormatters.toIsoString

class BookPageServiceImpl(
    private val bookRepository: BookRepository,
    private val textRepository: TextRepository,
) : BookPageService {
  override fun getBookPages(bookId: Long): List<TextDto> {
    bookRepository.findById(bookId) ?: throw EntityNotFoundException("Book", bookId)

    return textRepository.findByBookId(bookId).map { it.toDto() }
  }

  override fun createTextPages(bookId: Long, content: String) {
    val wordsPerPage = 250
    val pages = paginateContent(content, wordsPerPage)

    pages.forEachIndexed { index, pageText ->
      val text =
          Text(
              bookId = bookId,
              order = index + 1,
              text = pageText,
              readDate = null,
              wordCount = countWords(pageText),
              startDate = null,
          )
      textRepository.save(text)
    }
  }

  private fun paginateContent(content: String, wordsPerPage: Int): List<String> {
    if (content.isBlank()) return emptyList()

    val words = content.split(Regex("\\s+")).filter { it.isNotBlank() }
    if (words.isEmpty()) return emptyList()

    val pages = mutableListOf<String>()
    var currentPage = mutableListOf<String>()
    var currentCount = 0

    for (word in words) {
      currentPage.add(word)
      currentCount++

      if (currentCount >= wordsPerPage) {
        pages.add(currentPage.joinToString(" "))
        currentPage = mutableListOf()
        currentCount = 0
      }
    }

    if (currentPage.isNotEmpty()) {
      pages.add(currentPage.joinToString(" "))
    }

    return pages
  }

  private fun countWords(text: String): Int {
    return text.split(Regex("\\s+")).filter { it.isNotBlank() }.size
  }

  private fun Text.toDto(): TextDto {
    return TextDto(
        id = id,
        book_id = bookId,
        order = order,
        text = text,
        read_date = readDate.toIsoString(),
        word_count = wordCount,
    )
  }
}
