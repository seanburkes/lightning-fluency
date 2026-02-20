package com.lute.db.repositories

import com.lute.domain.Book
import com.lute.domain.BookStats
import com.lute.domain.Language
import com.lute.domain.Setting
import com.lute.domain.Status
import com.lute.domain.Tag
import com.lute.domain.Term
import com.lute.domain.Text

interface LanguageRepository {
  fun findById(id: Long): Language?

  fun findByName(name: String): Language?

  fun findAll(limit: Int = Int.MAX_VALUE, offset: Int = 0): List<Language>

  fun save(language: Language): Long

  fun update(language: Language)

  fun delete(id: Long)
}

interface BookRepository {
  fun findById(id: Long): Book?

  fun findAll(
      languageId: Long? = null,
      archived: Boolean? = null,
      limit: Int = Int.MAX_VALUE,
      offset: Int = 0,
  ): List<Book>

  fun save(book: Book): Long

  fun update(book: Book)

  fun updateCurrentPage(bookId: Long, txId: Long)

  fun delete(id: Long)

  fun saveAll(books: List<Book>): List<Long>

  fun deleteAll(ids: List<Long>)
}

interface TextRepository {
  fun findById(id: Long): Text?

  fun findByBookId(bookId: Long): List<Text>

  fun findByBookAndOrder(bookId: Long, order: Int): Text?

  fun getCountForBook(bookId: Long): Int

  fun save(text: Text): Long

  fun update(text: Text)

  fun delete(id: Long)

  fun saveAll(texts: List<Text>): List<Long>
}

interface TermRepository {
  fun findById(id: Long): Term?

  fun findByTextAndLanguage(textLC: String, languageId: Long): Term?

  fun findAll(
      languageId: Long? = null,
      status: Int? = null,
      limit: Int = Int.MAX_VALUE,
      offset: Int = 0,
  ): List<Term>

  fun save(term: Term): Long

  fun update(term: Term)

  fun delete(id: Long)

  fun countByLanguage(languageId: Long): Int

  fun saveAll(terms: List<Term>): List<Long>

  fun deleteAll(ids: List<Long>)
}

interface TagRepository {
  fun findAll(): List<Tag>

  fun findByText(text: String): Tag?

  fun save(tag: Tag): Long

  fun addTagToTerm(termId: Long, tagId: Long)

  fun removeTagFromTerm(termId: Long, tagId: Long)

  fun getTagsForTerm(termId: Long): List<Tag>
}

interface StatusRepository {
  fun findAll(): List<Status>

  fun findById(id: Long): Status?

  fun save(status: Status): Long
}

interface SettingsRepository {
  fun get(key: String): String?

  fun set(key: String, value: String, keyType: String = "str")

  fun getAll(): Map<String, Setting>
}

interface BookStatsRepository {
  fun findByBookId(bookId: Long): BookStats?

  fun update(bookStats: BookStats)

  fun calculateAndSave(bookId: Long)
}
