package com.lute.db.repositories

import com.lute.db.Mappers.toBook
import com.lute.db.tables.BooksTable
import com.lute.domain.Book
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.transactions.transaction

class BookRepositoryImpl : BookRepository {
  override fun findById(id: Long): Book? = transaction {
    BooksTable.selectAll().where { BooksTable.BkID eq id }.singleOrNull()?.toBook()
  }

  override fun findAll(
      languageId: Long?,
      archived: Boolean?,
      limit: Int,
      offset: Int,
  ): List<Book> = transaction {
    BooksTable.selectAll()
        .apply {
          val conditions = mutableListOf<Op<Boolean>>()
          languageId?.let { conditions.add(BooksTable.BkLgID eq it) }
          archived?.let { conditions.add(BooksTable.BkArchived eq if (it) 1 else 0) }
          if (conditions.isNotEmpty()) {
            where { conditions.reduce { acc, op -> acc and op } }
          }
        }
        .limit(limit)
        .offset(offset.toLong())
        .map { it.toBook() }
  }

  override fun save(book: Book): Long = transaction {
    BooksTable.insert {
          it[BkLgID] = book.languageId
          it[BkTitle] = book.title
          it[BkSourceURI] = book.sourceURI
          it[BkArchived] = if (book.archived) 1 else 0
          it[BkCurrentTxID] = book.currentTextId
          it[BkAudioFilename] = book.audioFilename
          it[BkAudioCurrentPos] = book.audioCurrentPos
          it[BkAudioBookmarks] = book.audioBookmarks
        }[BooksTable.BkID]
  }

  override fun update(book: Book): Unit = transaction {
    BooksTable.update({ BooksTable.BkID eq book.id }) {
      it[BkLgID] = book.languageId
      it[BkTitle] = book.title
      it[BkSourceURI] = book.sourceURI
      it[BkArchived] = if (book.archived) 1 else 0
      it[BkCurrentTxID] = book.currentTextId
      it[BkAudioFilename] = book.audioFilename
      it[BkAudioCurrentPos] = book.audioCurrentPos
      it[BkAudioBookmarks] = book.audioBookmarks
    }
  }

  override fun updateCurrentPage(bookId: Long, txId: Long): Unit = transaction {
    BooksTable.update({ BooksTable.BkID eq bookId }) { it[BkCurrentTxID] = txId }
  }

  override fun delete(id: Long): Unit = transaction { BooksTable.deleteWhere { BkID eq id } }

  override fun saveAll(books: List<Book>): List<Long> = transaction {
    books.map { book ->
      BooksTable.insert {
            it[BkLgID] = book.languageId
            it[BkTitle] = book.title
            it[BkSourceURI] = book.sourceURI
            it[BkArchived] = if (book.archived) 1 else 0
            it[BkCurrentTxID] = book.currentTextId
            it[BkAudioFilename] = book.audioFilename
            it[BkAudioCurrentPos] = book.audioCurrentPos
            it[BkAudioBookmarks] = book.audioBookmarks
          }[BooksTable.BkID]
    }
  }

  override fun deleteAll(ids: List<Long>): Unit = transaction {
    BooksTable.deleteWhere { BkID inList ids }
  }
}
