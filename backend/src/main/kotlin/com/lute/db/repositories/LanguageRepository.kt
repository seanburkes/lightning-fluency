package com.lute.db.repositories

import com.lute.db.Mappers.toLanguage
import com.lute.db.tables.LanguagesTable
import com.lute.domain.Language
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class LanguageRepository {
  fun findById(id: Long): Language? = transaction {
    LanguagesTable.selectAll().where { LanguagesTable.LgID eq id }.singleOrNull()?.toLanguage()
  }

  fun findByName(name: String): Language? = transaction {
    LanguagesTable.selectAll().where { LanguagesTable.LgName eq name }.singleOrNull()?.toLanguage()
  }

  fun findAll(limit: Int = Int.MAX_VALUE, offset: Int = 0): List<Language> = transaction {
    LanguagesTable.selectAll().limit(limit).offset(offset.toLong()).map { it.toLanguage() }
  }

  fun save(language: Language): Long = transaction {
    LanguagesTable.insert {
          it[LgName] = language.name
          it[LgCharacterSubstitutions] = language.characterSubstitutions
          it[LgRegexpSplitSentences] = language.regexpSplitSentences
          it[LgExceptionsSplitSentences] = language.exceptionsSplitSentences
          it[LgRegexpWordCharacters] = language.regexpWordCharacters
          it[LgRightToLeft] = if (language.rightToLeft) 1 else 0
          it[LgShowRomanization] = if (language.showRomanization) 1 else 0
          it[LgParserType] = language.parserType
        }[LanguagesTable.LgID]
  }

  fun update(language: Language): Unit = transaction {
    LanguagesTable.update({ LanguagesTable.LgID eq language.id }) {
      it[LgName] = language.name
      it[LgCharacterSubstitutions] = language.characterSubstitutions
      it[LgRegexpSplitSentences] = language.regexpSplitSentences
      it[LgExceptionsSplitSentences] = language.exceptionsSplitSentences
      it[LgRegexpWordCharacters] = language.regexpWordCharacters
      it[LgRightToLeft] = if (language.rightToLeft) 1 else 0
      it[LgShowRomanization] = if (language.showRomanization) 1 else 0
      it[LgParserType] = language.parserType
    }
  }

  fun delete(id: Long): Unit = transaction { LanguagesTable.deleteWhere { LgID eq id } }
}
