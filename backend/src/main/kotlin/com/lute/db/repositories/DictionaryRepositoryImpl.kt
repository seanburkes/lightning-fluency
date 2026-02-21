package com.lute.db.repositories

import com.lute.db.Mappers.toDictionary
import com.lute.db.tables.LanguageDictsTable
import com.lute.domain.Dictionary
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class DictionaryRepositoryImpl : DictionaryRepository {
  override fun findById(id: Long): Dictionary? = transaction {
    LanguageDictsTable.selectAll()
        .where { LanguageDictsTable.LdID eq id }
        .singleOrNull()
        ?.toDictionary()
  }

  override fun findByIdAndLanguageId(id: Long, languageId: Long): Dictionary? = transaction {
    LanguageDictsTable.selectAll()
        .where { (LanguageDictsTable.LdID eq id) and (LanguageDictsTable.LdLgID eq languageId) }
        .singleOrNull()
        ?.toDictionary()
  }

  override fun findByLanguageId(languageId: Long): List<Dictionary> = transaction {
    LanguageDictsTable.selectAll()
        .where { LanguageDictsTable.LdLgID eq languageId }
        .orderBy(LanguageDictsTable.LdSortOrder to SortOrder.ASC)
        .map { it.toDictionary() }
  }

  override fun save(dictionary: Dictionary): Long = transaction {
    LanguageDictsTable.insert {
          it[LdLgID] = dictionary.languageId
          it[LdUseFor] = dictionary.useFor
          it[LdType] = dictionary.type
          it[LdDictURI] = dictionary.dictUri
          it[LdIsActive] = if (dictionary.isActive) 1 else 0
          it[LdSortOrder] = dictionary.sortOrder
        }[LanguageDictsTable.LdID]
  }

  override fun update(dictionary: Dictionary): Unit = transaction {
    LanguageDictsTable.update({ LanguageDictsTable.LdID eq dictionary.id }) {
      it[LdLgID] = dictionary.languageId
      it[LdUseFor] = dictionary.useFor
      it[LdType] = dictionary.type
      it[LdDictURI] = dictionary.dictUri
      it[LdIsActive] = if (dictionary.isActive) 1 else 0
      it[LdSortOrder] = dictionary.sortOrder
    }
  }

  override fun delete(id: Long): Unit = transaction {
    LanguageDictsTable.deleteWhere { LdID eq id }
  }
}
