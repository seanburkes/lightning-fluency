package com.lute.application

import com.lute.dtos.PopupDto

interface PopupService {
  fun getPopupData(bookId: Long, word: String): PopupDto
}
