package com.lute.application

import com.lute.db.repositories.BookRepository
import java.io.File
import java.io.InputStream
import org.slf4j.LoggerFactory

class AudioServiceImpl(private val bookRepository: BookRepository) : AudioService {
  private val logger = LoggerFactory.getLogger(AudioServiceImpl::class.java)

  private val audioDir: File by lazy {
    val dataPath = System.getenv("LUTE_DATA_PATH") ?: "/data"
    val dir = File("$dataPath/audio")
    if (!dir.exists()) {
      dir.mkdirs()
      logger.info("Created audio directory: ${dir.absolutePath}")
    }
    dir
  }

  override fun getAudioDirectory(): File = audioDir

  override fun storeAudio(bookId: Long, filename: String, inputStream: InputStream): String? {
    val book = bookRepository.findById(bookId) ?: return null

    book.audioFilename?.let { existingFilename ->
      File(audioDir, existingFilename).takeIf { it.exists() }?.delete()
    }

    val uniqueFilename = "book_${bookId}_${sanitizeFilename(filename)}"
    val targetFile = File(audioDir, uniqueFilename)

    inputStream.use { input -> targetFile.outputStream().use { output -> input.copyTo(output) } }

    bookRepository.update(book.copy(audioFilename = uniqueFilename))

    logger.info("Stored audio file for book $bookId: $uniqueFilename")
    return uniqueFilename
  }

  override fun getAudio(bookId: Long): File? {
    val book = bookRepository.findById(bookId) ?: return null
    return book.audioFilename?.let { File(audioDir, it).takeIf { f -> f.exists() } }
  }

  override fun deleteAudio(bookId: Long): Boolean {
    val book = bookRepository.findById(bookId) ?: return false
    val audioFilename = book.audioFilename ?: return false

    val audioFile = File(audioDir, audioFilename)
    if (audioFile.exists()) {
      audioFile.delete()
    }

    bookRepository.update(book.copy(audioFilename = null, audioCurrentPos = null))
    logger.info("Deleted audio file for book $bookId")

    return true
  }

  private fun sanitizeFilename(filename: String): String =
      filename.replace(Regex("[^a-zA-Z0-9.\\-_]"), "_")
}
