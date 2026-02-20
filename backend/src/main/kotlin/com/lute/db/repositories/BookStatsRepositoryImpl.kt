package com.lute.db.repositories

import com.lute.db.Mappers.toBookStats
import com.lute.db.tables.BookStatsTable
import com.lute.db.tables.BooksTable
import com.lute.db.tables.WordsTable
import com.lute.domain.BookStats
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class BookStatsRepositoryImpl : BookStatsRepository {
  override fun findByBookId(bookId: Long): BookStats? = transaction {
    BookStatsTable.selectAll()
        .where { BookStatsTable.BsBkID eq bookId }
        .singleOrNull()
        ?.toBookStats()
  }

  override fun update(bookStats: BookStats): Unit = transaction {
    BookStatsTable.upsert {
      it[BsBkID] = bookStats.bookId
      it[BsDistinctTerms] = bookStats.distinctTerms
      it[BsDistinctUnknowns] = bookStats.distinctUnknowns
      it[BsUnknownPercent] = bookStats.unknownPercent
      it[BsStatusDistribution] = bookStats.statusDistribution
    }
  }

  override fun calculateAndSave(bookId: Long): Unit = transaction {
    val langId =
        BooksTable.selectAll()
            .where { BooksTable.BkID eq bookId }
            .singleOrNull()
            ?.get(BooksTable.BkLgID) ?: return@transaction

    val distinctTerms = WordsTable.selectAll().where { WordsTable.WoLgID eq langId }.count().toInt()

    val distinctUnknowns =
        WordsTable.selectAll()
            .where { (WordsTable.WoLgID eq langId) and (WordsTable.WoStatus eq 0) }
            .count()
            .toInt()

    val unknownPercent = if (distinctTerms > 0) (distinctUnknowns * 100) / distinctTerms else 0

    val stats =
        BookStats(
            bookId = bookId,
            distinctTerms = distinctTerms,
            distinctUnknowns = distinctUnknowns,
            unknownPercent = unknownPercent,
        )
    update(stats)
  }
}
