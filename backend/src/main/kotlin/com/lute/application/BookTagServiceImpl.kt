package com.lute.application

import com.lute.db.repositories.BookRepository
import com.lute.db.repositories.TagRepository

class BookTagServiceImpl(
    private val bookRepository: BookRepository,
    private val tagRepository: TagRepository,
) : BookTagService {
  override fun addTagToBook(bookId: Long, tagId: Long) {
    bookRepository.require(bookId, "Book")
    tagRepository.require(tagId, "Tag")

    bookRepository.addTagToBook(bookId, tagId)
  }

  override fun removeTagFromBook(bookId: Long, tagId: Long) {
    bookRepository.removeTagFromBook(bookId, tagId)
  }

  override fun getTagsForBook(bookId: Long): List<String> {
    return tagRepository.getTagsForBooks(listOf(bookId))?.get(bookId)?.map { it.text }
        ?: emptyList()
  }
}
