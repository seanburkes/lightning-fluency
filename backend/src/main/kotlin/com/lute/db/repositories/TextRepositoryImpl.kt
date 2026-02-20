package com.lute.db.repositories

import com.lute.db.Mappers.toText
import com.lute.db.tables.TextsTable
import com.lute.domain.Text
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class TextRepositoryImpl : TextRepository {
  override fun findById(id: Long): Text? = transaction {
    TextsTable.selectAll().where { TextsTable.TxID eq id }.singleOrNull()?.toText()
  }

  override fun findByBookId(bookId: Long): List<Text> = transaction {
    TextsTable.selectAll()
        .where { TextsTable.TxBkID eq bookId }
        .orderBy(TextsTable.TxOrder)
        .map { it.toText() }
  }

  override fun findByBookAndOrder(bookId: Long, order: Int): Text? = transaction {
    TextsTable.selectAll()
        .where { (TextsTable.TxBkID eq bookId) and (TextsTable.TxOrder eq order) }
        .singleOrNull()
        ?.toText()
  }

  override fun getCountForBook(bookId: Long): Int = transaction {
    TextsTable.selectAll().where { TextsTable.TxBkID eq bookId }.count().toInt()
  }

  override fun save(text: Text): Long = transaction {
    TextsTable.insert {
          it[TxBkID] = text.bookId
          it[TxOrder] = text.order
          it[TxText] = text.text
          it[TxReadDate] = text.readDate
          it[TxWordCount] = text.wordCount
          it[TxStartDate] = text.startDate
        }[TextsTable.TxID]
  }

  override fun update(text: Text): Unit = transaction {
    TextsTable.update({ TextsTable.TxID eq text.id }) {
      it[TxBkID] = text.bookId
      it[TxOrder] = text.order
      it[TxText] = text.text
      it[TxReadDate] = text.readDate
      it[TxWordCount] = text.wordCount
      it[TxStartDate] = text.startDate
    }
  }

  override fun delete(id: Long): Unit = transaction { TextsTable.deleteWhere { TxID eq id } }

  override fun saveAll(texts: List<Text>): List<Long> = transaction {
    texts.map { text ->
      TextsTable.insert {
            it[TxBkID] = text.bookId
            it[TxOrder] = text.order
            it[TxText] = text.text
            it[TxReadDate] = text.readDate
            it[TxWordCount] = text.wordCount
            it[TxStartDate] = text.startDate
          }[TextsTable.TxID]
    }
  }
}
