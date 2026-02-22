package com.lute.application

import com.lute.dtos.TextDto

interface BookPageService {
  fun getBookPages(bookId: Long): List<TextDto>

  fun createTextPages(bookId: Long, content: String)
}
