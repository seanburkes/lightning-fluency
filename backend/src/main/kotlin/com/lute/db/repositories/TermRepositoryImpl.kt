package com.lute.db.repositories

import com.lute.db.Mappers.toTerm
import com.lute.db.tables.WordsTable
import com.lute.domain.Term
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.transactions.transaction

class TermRepositoryImpl : TermRepository {
  override fun findById(id: Long): Term? = transaction {
    WordsTable.selectAll().where { WordsTable.WoID eq id }.singleOrNull()?.toTerm()
  }

  override fun findByTextAndLanguage(textLC: String, languageId: Long): Term? = transaction {
    WordsTable.selectAll()
        .where { (WordsTable.WoTextLC eq textLC) and (WordsTable.WoLgID eq languageId) }
        .singleOrNull()
        ?.toTerm()
  }

  override fun findAll(
      languageId: Long?,
      status: Int?,
      limit: Int,
      offset: Int,
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

  override fun save(term: Term): Long = transaction {
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

  override fun update(term: Term): Unit = transaction {
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

  override fun delete(id: Long): Unit = transaction { WordsTable.deleteWhere { WoID eq id } }

  override fun countByLanguage(languageId: Long): Int = transaction {
    WordsTable.selectAll().where { WordsTable.WoLgID eq languageId }.count().toInt()
  }

  override fun saveAll(terms: List<Term>): List<Long> = transaction {
    terms.map { term ->
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
  }

  override fun deleteAll(ids: List<Long>): Unit = transaction {
    WordsTable.deleteWhere { WoID inList ids }
  }
}
