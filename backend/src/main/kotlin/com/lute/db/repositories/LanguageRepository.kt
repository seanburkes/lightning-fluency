package com.lute.db.repositories

import com.lute.db.Mappers.toLanguage
import com.lute.db.tables.LanguagesTable
import com.lute.domain.Language
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class LanguageRepository {
  fun findById(id: Int): Language? = transaction {
    LanguagesTable.selectAll().where { LanguagesTable.LgID eq id }.singleOrNull()?.toLanguage()
  }

  fun findByName(name: String): Language? = transaction {
    LanguagesTable.selectAll().where { LanguagesTable.LgName eq name }.singleOrNull()?.toLanguage()
  }

  fun findAll(): List<Language> = transaction { LanguagesTable.selectAll().map { it.toLanguage() } }

  fun save(language: Language): Int = transaction {
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

  fun delete(id: Int): Unit = transaction { LanguagesTable.deleteWhere { LgID eq id } }
}
