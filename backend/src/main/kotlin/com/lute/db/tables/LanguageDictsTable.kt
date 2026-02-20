package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object LanguageDictsTable : Table("languagedicts") {
  val LdID = long("LdID").autoIncrement()
  val LdLgID = long("LdLgID").references(LanguagesTable.LgID)
  val LdUseFor = varchar("LdUseFor", 20)
  val LdType = varchar("LdType", 20)
  val LdDictURI = varchar("LdDictURI", 200)
  val LdIsActive = integer("LdIsActive").default(1)
  val LdSortOrder = integer("LdSortOrder")

  init {
    index(customIndexName = "idx_languagedicts_lgid", columns = arrayOf(LdLgID))
    index(customIndexName = "idx_languagedicts_usefor", columns = arrayOf(LdUseFor))
    index(customIndexName = "idx_languagedicts_type", columns = arrayOf(LdType))
    index(customIndexName = "idx_languagedicts_isactive", columns = arrayOf(LdIsActive))
  }

  override val primaryKey = PrimaryKey(LdID)
}
