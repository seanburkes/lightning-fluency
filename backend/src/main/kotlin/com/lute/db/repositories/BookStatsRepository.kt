package com.lute.db.repositories

import com.lute.db.Mappers.toBookStats
import com.lute.db.tables.BookStatsTable
import com.lute.db.tables.BooksTable
import com.lute.db.tables.WordsTable
import com.lute.domain.BookStats
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class BookStatsRepository {
  fun findByBookId(bookId: Int): BookStats? = transaction {
    BookStatsTable.selectAll().where { BookStatsTable.BkID eq bookId }.singleOrNull()?.toBookStats()
  }

  fun update(bookStats: BookStats): Unit = transaction {
    val exists = BookStatsTable.selectAll().where { BookStatsTable.BkID eq bookStats.bookId }.any()
    if (exists) {
      BookStatsTable.update({ BookStatsTable.BkID eq bookStats.bookId }) {
        it[distinctterms] = bookStats.distinctTerms
        it[distinctunknowns] = bookStats.distinctUnknowns
        it[unknownpercent] = bookStats.unknownPercent
        it[status_distribution] = bookStats.statusDistribution
      }
    } else {
      BookStatsTable.insert {
        it[BkID] = bookStats.bookId
        it[distinctterms] = bookStats.distinctTerms
        it[distinctunknowns] = bookStats.distinctUnknowns
        it[unknownpercent] = bookStats.unknownPercent
        it[status_distribution] = bookStats.statusDistribution
      }
    }
  }

  fun calculateAndSave(bookId: Int): Unit = transaction {
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
