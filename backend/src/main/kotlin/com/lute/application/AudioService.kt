package com.lute.application

import java.io.File
import java.io.InputStream

interface AudioService {
  fun storeAudio(bookId: Long, filename: String, inputStream: InputStream): String?

  fun getAudio(bookId: Long): File?

  fun deleteAudio(bookId: Long): Boolean

  fun getAudioDirectory(): File
}
