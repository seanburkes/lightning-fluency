package com.lute.db.repositories

import com.lute.db.Mappers.toTerm
import com.lute.db.tables.WordParentsTable
import com.lute.db.tables.WordTagsTable
import com.lute.db.tables.WordsTable
import com.lute.domain.Term
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
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

  override fun findByIds(ids: List<Long>): List<Term> = transaction {
    if (ids.isEmpty()) {
      emptyList()
    } else {
      WordsTable.selectAll().where { WordsTable.WoID inList ids }.map { it.toTerm() }
    }
  }

  override fun findByTextContaining(
      query: String,
      languageId: Long?,
      status: Int?,
  ): List<Term> = transaction {
    val conditions = mutableListOf<Op<Boolean>>()
    val escapedQuery = query.lowercase().replace("%", "\\%").replace("_", "\\_")
    conditions.add(WordsTable.WoTextLC like "%$escapedQuery%")
    languageId?.let { conditions.add(WordsTable.WoLgID eq it) }
    status?.let { conditions.add(WordsTable.WoStatus eq it) }

    WordsTable.selectAll().where { conditions.reduce { acc, op -> acc and op } }.map { it.toTerm() }
  }

  override fun getParentIdsForTerms(termIds: List<Long>): Map<Long, List<Long>> = transaction {
    if (termIds.isEmpty()) {
      emptyMap()
    } else {
      val rows = WordParentsTable.selectAll().where { WordParentsTable.WpWoID inList termIds }
      val result = mutableMapOf<Long, MutableList<Long>>()
      for (row in rows) {
        val termId = row[WordParentsTable.WpWoID]
        val parentId = row[WordParentsTable.WpParentWoID]
        result.getOrPut(termId) { mutableListOf() }.add(parentId)
      }
      result
    }
  }

  override fun getChildrenCountForTerms(termIds: List<Long>): Map<Long, Int> = transaction {
    if (termIds.isEmpty()) {
      emptyMap()
    } else {
      val rows = WordParentsTable.selectAll().where { WordParentsTable.WpParentWoID inList termIds }
      val result = mutableMapOf<Long, Int>()
      for (row in rows) {
        val parentId = row[WordParentsTable.WpParentWoID]
        result[parentId] = (result[parentId] ?: 0) + 1
      }
      result
    }
  }

  override fun deleteWithRelationships(id: Long): Unit = transaction {
    WordTagsTable.deleteWhere { WordTagsTable.WtWoID eq id }
    WordParentsTable.deleteWhere { WordParentsTable.WpWoID eq id }
    WordParentsTable.deleteWhere { WordParentsTable.WpParentWoID eq id }
    WordsTable.deleteWhere { WordsTable.WoID eq id }
  }

  override fun addParent(termId: Long, parentId: Long): Unit = transaction {
    val existing =
        WordParentsTable.selectAll()
            .where {
              (WordParentsTable.WpWoID eq termId) and (WordParentsTable.WpParentWoID eq parentId)
            }
            .singleOrNull()
    if (existing == null) {
      WordParentsTable.insert {
        it[WordParentsTable.WpWoID] = termId
        it[WordParentsTable.WpParentWoID] = parentId
      }
    }
  }

  override fun removeParent(termId: Long, parentId: Long): Unit = transaction {
    WordParentsTable.deleteWhere {
      (WordParentsTable.WpWoID eq termId) and (WordParentsTable.WpParentWoID eq parentId)
    }
  }
}
