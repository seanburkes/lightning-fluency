package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object LanguagesTable : Table("languages") {
  val LgID = integer("LgID").autoIncrement()
  val LgName = varchar("LgName", 40)
  val LgCharacterSubstitutions = varchar("LgCharacterSubstitutions", 500).nullable()
  val LgRegexpSplitSentences = varchar("LgRegexpSplitSentences", 500).nullable()
  val LgExceptionsSplitSentences = varchar("LgExceptionsSplitSentences", 500).nullable()
  val LgRegexpWordCharacters = varchar("LgRegexpWordCharacters", 500).nullable()
  val LgRightToLeft = integer("LgRightToLeft").default(0)
  val LgShowRomanization = integer("LgShowRomanization").default(0)
  val LgParserType = varchar("LgParserType", 20).default("spacedel")

  override val primaryKey = PrimaryKey(LgID)
}
