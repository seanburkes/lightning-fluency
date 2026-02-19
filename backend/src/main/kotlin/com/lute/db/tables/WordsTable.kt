package com.lute.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object WordsTable : Table("words") {
  val WoID = integer("WoID").autoIncrement()
  val WoLgID = integer("WoLgID").references(LanguagesTable.LgID)
  val WoText = varchar("WoText", 250)
  val WoTextLC = varchar("WoTextLC", 250)
  val WoStatus = integer("WoStatus").default(0)
  val WoTranslation = varchar("WoTranslation", 500).nullable()
  val WoRomanization = varchar("WoRomanization", 100).nullable()
  val WoTokenCount = integer("WoTokenCount").default(1)
  val WoCreated = datetime("WoCreated")
  val WoStatusChanged = datetime("WoStatusChanged")
  val WoSyncStatus = integer("WoSyncStatus").default(0)

  override val primaryKey = PrimaryKey(WoID)
}
