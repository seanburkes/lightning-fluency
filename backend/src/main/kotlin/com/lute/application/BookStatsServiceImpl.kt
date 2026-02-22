package com.lute.application

import com.lute.db.repositories.BookRepository
import com.lute.db.repositories.BookStatsRepository
import com.lute.dtos.BookStatsDto

class BookStatsServiceImpl(
    private val bookRepository: BookRepository,
    private val bookStatsRepository: BookStatsRepository,
) : BookStatsService {
  override fun getStats(bookId: Long): BookStatsDto? {
    val book = bookRepository.findById(bookId) ?: return null
    val stats = bookStatsRepository.findByBookId(bookId) ?: return null

    return BookStatsDto(
        book_id = bookId,
        distinct_terms = stats.distinctTerms ?: 0,
        distinct_unknowns = stats.distinctUnknowns ?: 0,
        unknown_percent = stats.unknownPercent ?: 0,
        status_distribution = parseStatusDistribution(stats.statusDistribution),
    )
  }

  override fun calculateAndSave(bookId: Long): BookStatsDto {
    bookStatsRepository.calculateAndSave(bookId)
    return getStats(bookId)
        ?: throw IllegalStateException("Stats not found after calculation for book $bookId")
  }

  private fun parseStatusDistribution(distribution: String?): Map<String, Int> {
    if (distribution.isNullOrBlank()) return emptyMap()
    return try {
      distribution
          .split(";")
          .filter { it.contains(":") }
          .associate {
            val parts = it.split(":")
            parts[0] to (parts.getOrNull(1)?.toIntOrNull() ?: 0)
          }
    } catch (e: Exception) {
      emptyMap()
    }
  }
}
