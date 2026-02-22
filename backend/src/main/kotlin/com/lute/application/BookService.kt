package com.lute.application

import com.lute.dtos.BookDto
import com.lute.dtos.CreateBookDto
import com.lute.dtos.TextDto
import com.lute.dtos.UpdateBookDto

interface BookService {
  fun getAllBooks(languageId: Long? = null, archived: Boolean? = null): List<BookDto>

  fun getBookById(id: Long): BookDto?

  fun createBook(dto: CreateBookDto): BookDto

  fun updateBook(id: Long, dto: UpdateBookDto): BookDto?

  fun deleteBook(id: Long): Boolean

  fun getBookPages(bookId: Long): List<TextDto>

  fun addTagToBook(bookId: Long, tagId: Long)

  fun removeTagFromBook(bookId: Long, tagId: Long)

  fun getTagsForBook(bookId: Long): List<String>
}
