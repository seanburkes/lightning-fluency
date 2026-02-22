package com.lute.application

import com.lute.dtos.BookDto
import com.lute.dtos.CreateBookDto
import com.lute.dtos.UpdateBookDto

interface BookCrudService {
  fun getAllBooks(languageId: Long?, archived: Boolean?): List<BookDto>

  fun getBookById(id: Long): BookDto?

  fun createBook(dto: CreateBookDto): BookDto

  fun updateBook(id: Long, dto: UpdateBookDto): BookDto?

  fun deleteBook(id: Long): Boolean
}
