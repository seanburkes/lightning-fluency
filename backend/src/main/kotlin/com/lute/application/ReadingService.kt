package com.lute.application

import com.lute.dtos.ReadingPageDto
import com.lute.dtos.TokenDto

interface ReadingService {
  fun getPage(bookId: Long, pageNum: Int): ReadingPageDto?

  fun parsePageWithTerms(text: String, languageId: Long): List<TokenDto>

  fun getNextPage(bookId: Long, currentPage: Int): Int?

  fun getPreviousPage(bookId: Long, currentPage: Int): Int?

  fun saveCurrentPage(bookId: Long, pageNum: Int)

  fun getCurrentPage(bookId: Long): Int?
}
