package com.lute.application

import com.lute.db.repositories.BookRepository
import com.lute.db.repositories.LanguageRepository
import com.lute.db.repositories.TagRepository
import com.lute.db.repositories.TextRepository
import com.lute.domain.Book
import com.lute.dtos.BookDto
import com.lute.dtos.CreateBookDto
import com.lute.dtos.UpdateBookDto

class BookCrudServiceImpl(
    private val bookRepository: BookRepository,
    private val textRepository: TextRepository,
    private val languageRepository: LanguageRepository,
    private val tagRepository: TagRepository,
    private val pageService: BookPageService,
) : BookCrudService {
  override fun getAllBooks(languageId: Long?, archived: Boolean?): List<BookDto> {
    val books = bookRepository.findAll(languageId = languageId, archived = archived)
    return booksToDtos(books)
  }

  override fun getBookById(id: Long): BookDto? {
    val book = bookRepository.findById(id) ?: return null
    return booksToDtos(listOf(book)).firstOrNull()
  }

  override fun createBook(dto: CreateBookDto): BookDto {
    languageRepository.require(dto.language_id)

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
        pageService.createTextPages(savedBook.id, content)
      }
    }

    return booksToDtos(listOf(savedBook)).first()
  }

  override fun updateBook(id: Long, dto: UpdateBookDto): BookDto? {
    val existing = bookRepository.findById(id) ?: return null

    val updated =
        existing.copy(
            title = dto.title ?: existing.title,
            archived = dto.archived ?: existing.archived,
        )

    bookRepository.update(updated)

    return booksToDtos(listOf(updated)).firstOrNull()
  }

  override fun deleteBook(id: Long): Boolean {
    bookRepository.findById(id) ?: return false
    bookRepository.deleteWithRelationships(id)
    return true
  }

  private fun booksToDtos(books: List<Book>): List<BookDto> {
    if (books.isEmpty()) return emptyList()

    val bookIds = books.map { it.id }
    val languageIds = books.map { it.languageId }.distinct()

    val languages = languageRepository.findByIds(languageIds)
    val languageMap = languages.associate { it.id to it.name }

    val pageCounts = textRepository.getCountsForBooks(bookIds)
    val tagsMap = tagRepository.getTagsForBooks(bookIds)

    return books.map { book ->
      book.toDto(
          languageName = languageMap[book.languageId] ?: "Unknown",
          pageCount = pageCounts[book.id] ?: 0,
          tags = tagsMap[book.id]?.map { it.text } ?: emptyList(),
      )
    }
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
}
