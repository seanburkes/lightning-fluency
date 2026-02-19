package com.lute.db.repositories

import com.lute.db.Mappers.toTerm
import com.lute.db.tables.WordsTable
import com.lute.domain.Term
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class TermRepository {
  fun findById(id: Int): Term? = transaction {
    WordsTable.selectAll().where { WordsTable.WoID eq id }.singleOrNull()?.toTerm()
  }

  fun findByTextAndLanguage(textLC: String, languageId: Int): Term? = transaction {
    WordsTable.selectAll()
        .where { (WordsTable.WoTextLC eq textLC) and (WordsTable.WoLgID eq languageId) }
        .singleOrNull()
        ?.toTerm()
  }

  fun findAll(
      languageId: Int? = null,
      status: Int? = null,
      limit: Int = Int.MAX_VALUE,
      offset: Int = 0,
  ): List<Term> = transaction {
    WordsTable.selectAll()
        .apply {
          val conditions = mutableListOf<Op<Boolean>>()
          languageId?.let { conditions.add(WordsTable.WoLgID eq it) }
          status?.let { conditions.add(WordsTable.WoStatus eq it) }
          if (conditions.isNotEmpty()) {
            where { conditions.reduce { acc, op -> acc and op } }
          }
        }
        .limit(limit)
        .offset(offset.toLong())
        .map { it.toTerm() }
  }

  fun save(term: Term): Int = transaction {
    val effectiveTextLC = term.textLC.ifEmpty { term.text.lowercase() }
    WordsTable.insert {
          it[WoLgID] = term.languageId
          it[WoText] = term.text
          it[WoTextLC] = effectiveTextLC
          it[WoStatus] = term.status
          it[WoTranslation] = term.translation
          it[WoRomanization] = term.romanization
          it[WoTokenCount] = term.tokenCount
          it[WoSyncStatus] = term.syncStatus
        }[WordsTable.WoID]
  }

  fun update(term: Term): Unit = transaction {
    WordsTable.update({ WordsTable.WoID eq term.id }) {
      it[WoLgID] = term.languageId
      it[WoText] = term.text
      it[WoTextLC] = term.textLC.ifEmpty { term.text.lowercase() }
      it[WoStatus] = term.status
      it[WoTranslation] = term.translation
      it[WoRomanization] = term.romanization
      it[WoTokenCount] = term.tokenCount
      it[WoSyncStatus] = term.syncStatus
    }
  }

  fun delete(id: Int): Unit = transaction { WordsTable.deleteWhere { WoID eq id } }

  fun countByLanguage(languageId: Int): Int = transaction {
    WordsTable.selectAll().where { WordsTable.WoLgID eq languageId }.count().toInt()
  }
}
