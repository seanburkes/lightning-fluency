package com.lute.db

import com.lute.db.tables.*
import com.lute.domain.*
import org.jetbrains.exposed.sql.ResultRow

object Mappers {
  fun ResultRow.toDictionary() =
      Dictionary(
          id = this[LanguageDictsTable.LdID],
          languageId = this[LanguageDictsTable.LdLgID],
          useFor = this[LanguageDictsTable.LdUseFor],
          type = this[LanguageDictsTable.LdType],
          dictUri = this[LanguageDictsTable.LdDictURI],
          isActive = this[LanguageDictsTable.LdIsActive] != 0,
          sortOrder = this[LanguageDictsTable.LdSortOrder],
      )

  fun ResultRow.toLanguage() =
      Language(
          id = this[LanguagesTable.LgID],
          name = this[LanguagesTable.LgName],
          characterSubstitutions = this[LanguagesTable.LgCharacterSubstitutions],
          regexpSplitSentences = this[LanguagesTable.LgRegexpSplitSentences],
          exceptionsSplitSentences = this[LanguagesTable.LgExceptionsSplitSentences],
          regexpWordCharacters = this[LanguagesTable.LgRegexpWordCharacters],
          rightToLeft = this[LanguagesTable.LgRightToLeft] != 0,
          showRomanization = this[LanguagesTable.LgShowRomanization] != 0,
          parserType = this[LanguagesTable.LgParserType],
      )

  fun ResultRow.toBook() =
      Book(
          id = this[BooksTable.BkID],
          languageId = this[BooksTable.BkLgID],
          title = this[BooksTable.BkTitle],
          sourceURI = this[BooksTable.BkSourceURI],
          archived = this[BooksTable.BkArchived] != 0,
          currentTextId = this[BooksTable.BkCurrentTxID],
          audioFilename = this[BooksTable.BkAudioFilename],
          audioCurrentPos = this[BooksTable.BkAudioCurrentPos],
          audioBookmarks = this[BooksTable.BkAudioBookmarks],
      )

  fun ResultRow.toText() =
      Text(
          id = this[TextsTable.TxID],
          bookId = this[TextsTable.TxBkID],
          order = this[TextsTable.TxOrder],
          text = this[TextsTable.TxText],
          readDate = this[TextsTable.TxReadDate],
          wordCount = this[TextsTable.TxWordCount],
          startDate = this[TextsTable.TxStartDate],
      )

  fun ResultRow.toTerm() =
      Term(
          id = this[WordsTable.WoID],
          languageId = this[WordsTable.WoLgID],
          text = this[WordsTable.WoText],
          textLC = this[WordsTable.WoTextLC],
          status = this[WordsTable.WoStatus],
          translation = this[WordsTable.WoTranslation],
          romanization = this[WordsTable.WoRomanization],
          tokenCount = this[WordsTable.WoTokenCount],
          created = this[WordsTable.WoCreated],
          statusChanged = this[WordsTable.WoStatusChanged],
          syncStatus = this[WordsTable.WoSyncStatus],
      )

  fun ResultRow.toTag() =
      Tag(
          id = this[TagsTable.TgID],
          text = this[TagsTable.TgText],
          comment = this[TagsTable.TgComment],
      )

  fun ResultRow.toStatus() =
      Status(
          id = this[StatusesTable.StID],
          text = this[StatusesTable.StText],
          abbreviation = this[StatusesTable.StAbbreviation],
      )

  fun ResultRow.toSetting() =
      Setting(
          key = this[SettingsTable.StKey],
          keyType = this[SettingsTable.StKeyType],
          value = this[SettingsTable.StValue],
      )

  fun ResultRow.toBookStats() =
      BookStats(
          bookId = this[BookStatsTable.BsBkID],
          distinctTerms = this[BookStatsTable.BsDistinctTerms],
          distinctUnknowns = this[BookStatsTable.BsDistinctUnknowns],
          unknownPercent = this[BookStatsTable.BsUnknownPercent],
          statusDistribution = this[BookStatsTable.BsStatusDistribution],
      )
}
