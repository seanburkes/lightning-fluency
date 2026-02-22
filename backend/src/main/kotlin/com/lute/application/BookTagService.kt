package com.lute.application

interface BookTagService {
  fun addTagToBook(bookId: Long, tagId: Long)

  fun removeTagFromBook(bookId: Long, tagId: Long)

  fun getTagsForBook(bookId: Long): List<String>
}
