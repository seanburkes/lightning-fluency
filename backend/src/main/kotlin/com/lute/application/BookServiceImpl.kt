package com.lute.application

import com.lute.application.exceptions.BookNotFoundException
import com.lute.application.exceptions.LanguageNotFoundException
import com.lute.application.exceptions.TagNotFoundException
import com.lute.db.repositories.BookRepository
import com.lute.db.repositories.LanguageRepository
import com.lute.db.repositories.TagRepository
import com.lute.db.repositories.TextRepository
import com.lute.db.tables.BookTagsTable
import com.lute.db.tables.Tags2Table
import com.lute.domain.Book
import com.lute.domain.Text
import com.lute.dtos.BookDto
import com.lute.dtos.CreateBookDto
import com.lute.dtos.TextDto
import com.lute.dtos.UpdateBookDto
import java.time.format.DateTimeFormatter
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class BookServiceImpl(
    private val bookRepository: BookRepository,
    private val textRepository: TextRepository,
    private val languageRepository: LanguageRepository,
    private val tagRepository: TagRepository,
) : BookService {
  private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  override fun getAllBooks(languageId: Long?, archived: Boolean?): List<BookDto> {
    return bookRepository.findAll(languageId = languageId, archived = archived).map { book ->
      val language = languageRepository.findById(book.languageId)
      val pageCount = textRepository.getCountForBook(book.id)
      val tags = getTagsForBook(book.id)
      book.toDto(language?.name ?: "Unknown", pageCount, tags)
    }
  }

  override fun getBookById(id: Long): BookDto? {
    val book = bookRepository.findById(id) ?: return null
    val language = languageRepository.findById(book.languageId)
    val pageCount = textRepository.getCountForBook(book.id)
    val tags = getTagsForBook(book.id)
    return book.toDto(language?.name ?: "Unknown", pageCount, tags)
  }

  override fun createBook(dto: CreateBookDto): BookDto {
    val language =
        languageRepository.findById(dto.language_id)
            ?: throw LanguageNotFoundException("Language with id ${dto.language_id} not found")

    return transaction {
      val book =
          Book(
              languageId = dto.language_id,
              title = dto.title,
              sourceURI = dto.source_uri,
              archived = false,
              currentTextId = 0,
          )

      val id = bookRepository.save(book)
      val savedBook = book.copy(id = id)

      dto.content?.let { content ->
        if (content.isNotBlank()) {
          createTextPages(savedBook.id, content)
        }
      }

      savedBook.toDto(language.name, textRepository.getCountForBook(savedBook.id), emptyList())
    }
  }

  override fun updateBook(id: Long, dto: UpdateBookDto): BookDto? {
    val existing = bookRepository.findById(id) ?: return null

    val updated =
        existing.copy(
            title = dto.title ?: existing.title,
            archived = dto.archived ?: existing.archived,
        )

    bookRepository.update(updated)

    val language = languageRepository.findById(updated.languageId)
    val pageCount = textRepository.getCountForBook(updated.id)
    val tags = getTagsForBook(updated.id)
    return updated.toDto(language?.name ?: "Unknown", pageCount, tags)
  }

  override fun deleteBook(id: Long): Boolean {
    val book = bookRepository.findById(id) ?: return false

    transaction {
      BookTagsTable.deleteWhere { BtBkID eq id }
      val texts = textRepository.findByBookId(book.id)
      texts.forEach { textRepository.delete(it.id) }
      bookRepository.delete(id)
    }
    return true
  }

  override fun getBookPages(bookId: Long): List<TextDto> {
    val book =
        bookRepository.findById(bookId)
            ?: throw BookNotFoundException("Book with id $bookId not found")

    return textRepository.findByBookId(bookId).map { it.toDto() }
  }

  override fun addTagToBook(bookId: Long, tagId: Long) {
    bookRepository.findById(bookId) ?: throw BookNotFoundException("Book with id $bookId not found")

    transaction {
      Tags2Table.selectAll().where { Tags2Table.T2ID eq tagId }.singleOrNull()
          ?: throw TagNotFoundException("Tag with id $tagId not found")

      val existing =
          BookTagsTable.selectAll()
              .where { (BookTagsTable.BtBkID eq bookId) and (BookTagsTable.BtT2ID eq tagId) }
              .singleOrNull()

      if (existing == null) {
        BookTagsTable.insert {
          it[BtBkID] = bookId
          it[BtT2ID] = tagId
        }
      }
    }
  }

  override fun removeTagFromBook(bookId: Long, tagId: Long) {
    transaction { BookTagsTable.deleteWhere { (BtBkID eq bookId) and (BtT2ID eq tagId) } }
  }

  override fun getTagsForBook(bookId: Long): List<String> {
    return transaction {
      (BookTagsTable innerJoin Tags2Table)
          .selectAll()
          .where { BookTagsTable.BtBkID eq bookId }
          .map { it[Tags2Table.T2Text] }
    }
  }

  private fun createTextPages(bookId: Long, content: String) {
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

  private fun Book.toDto(languageName: String, pageCount: Int, tags: List<String>): BookDto {
    return BookDto(
        id = id,
        title = title,
        language_id = languageId,
        language_name = languageName,
        source_uri = sourceURI,
        archived = archived,
        page_count = pageCount,
        current_page = if (currentTextId > 0) currentTextId.toInt() else 0,
        tags = tags,
        created_at = null,
    )
  }

  private fun Text.toDto(): TextDto {
    return TextDto(
        id = id,
        book_id = bookId,
        order = order,
        text = text,
        read_date = readDate?.format(dateFormatter),
        word_count = wordCount,
    )
  }
}
