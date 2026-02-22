package com.lute.application

import com.lute.dtos.BookStatsDto

interface BookStatsService {
  fun getStats(bookId: Long): BookStatsDto?

  fun calculateAndSave(bookId: Long): BookStatsDto
}
