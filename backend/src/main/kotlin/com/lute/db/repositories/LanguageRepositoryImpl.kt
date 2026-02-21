package com.lute.db.repositories

import com.lute.db.Mappers.toLanguage
import com.lute.db.tables.BooksTable
import com.lute.db.tables.LanguageDictsTable
import com.lute.db.tables.LanguagesTable
import com.lute.db.tables.WordsTable
import com.lute.domain.Language
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class LanguageRepositoryImpl : LanguageRepository {
  override fun findById(id: Long): Language? = transaction {
    LanguagesTable.selectAll().where { LanguagesTable.LgID eq id }.singleOrNull()?.toLanguage()
  }

  override fun findByName(name: String): Language? = transaction {
    LanguagesTable.selectAll().where { LanguagesTable.LgName eq name }.singleOrNull()?.toLanguage()
  }

  override fun findAll(limit: Int, offset: Int): List<Language> = transaction {
    LanguagesTable.selectAll().limit(limit).offset(offset.toLong()).map { it.toLanguage() }
  }

  override fun save(language: Language): Long = transaction {
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

  override fun update(language: Language): Unit = transaction {
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

  override fun delete(id: Long): Unit = transaction { LanguagesTable.deleteWhere { LgID eq id } }

  override fun countBooksForLanguage(id: Long): Long = transaction {
    BooksTable.selectAll().where { BooksTable.BkLgID eq id }.count()
  }

  override fun countTermsForLanguage(id: Long): Long = transaction {
    WordsTable.selectAll().where { WordsTable.WoLgID eq id }.count()
  }

  override fun countDictionariesForLanguage(id: Long): Long = transaction {
    LanguageDictsTable.selectAll().where { LanguageDictsTable.LdLgID eq id }.count()
  }
}
