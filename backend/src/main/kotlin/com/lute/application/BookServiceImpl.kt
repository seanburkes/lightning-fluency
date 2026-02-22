package com.lute.application

import com.lute.dtos.BookDto
import com.lute.dtos.CreateBookDto
import com.lute.dtos.TextDto
import com.lute.dtos.UpdateBookDto

class BookServiceImpl(
    private val crudService: BookCrudService,
    private val pageService: BookPageService,
    private val tagService: BookTagService,
) : BookService {
  override fun getAllBooks(languageId: Long?, archived: Boolean?): List<BookDto> =
      crudService.getAllBooks(languageId, archived)

  override fun getBookById(id: Long): BookDto? = crudService.getBookById(id)

  override fun createBook(dto: CreateBookDto): BookDto = crudService.createBook(dto)

  override fun updateBook(id: Long, dto: UpdateBookDto): BookDto? = crudService.updateBook(id, dto)

  override fun deleteBook(id: Long): Boolean = crudService.deleteBook(id)

  override fun getBookPages(bookId: Long): List<TextDto> = pageService.getBookPages(bookId)

  override fun addTagToBook(bookId: Long, tagId: Long) = tagService.addTagToBook(bookId, tagId)

  override fun removeTagFromBook(bookId: Long, tagId: Long) =
      tagService.removeTagFromBook(bookId, tagId)

  override fun getTagsForBook(bookId: Long): List<String> = tagService.getTagsForBook(bookId)
}
