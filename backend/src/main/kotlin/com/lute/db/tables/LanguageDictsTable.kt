package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object LanguageDictsTable : Table("languagedicts") {
  val LdID = integer("LdID").autoIncrement()
  val LdLgID = integer("LdLgID").references(LanguagesTable.LgID)
  val LdUseFor = varchar("LdUseFor", 20)
  val LdType = varchar("LdType", 20)
  val LdDictURI = varchar("LdDictURI", 200)
  val LdIsActive = integer("LdIsActive").default(1)
  val LdSortOrder = integer("LdSortOrder")

  override val primaryKey = PrimaryKey(LdID)
}
