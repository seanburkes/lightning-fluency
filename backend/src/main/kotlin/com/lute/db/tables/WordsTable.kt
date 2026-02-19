package com.lute.db.tables

import java.time.LocalDateTime
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
  val WoCreated = datetime("WoCreated").clientDefault { LocalDateTime.now() }
  val WoStatusChanged = datetime("WoStatusChanged").clientDefault { LocalDateTime.now() }
  val WoSyncStatus = integer("WoSyncStatus").default(0)

  init {
    index(customIndexName = "idx_words_lgid", columns = arrayOf(WoLgID))
    index(customIndexName = "idx_words_status", columns = arrayOf(WoStatus))
    index(customIndexName = "idx_words_status_changed", columns = arrayOf(WoStatusChanged))
    index(customIndexName = "idx_words_textlc", columns = arrayOf(WoTextLC))
    uniqueIndex("idx_words_textlc_lgid", WoTextLC, WoLgID)
    index(
        customIndexName = "idx_words_lgid_status_textlc",
        columns = arrayOf(WoLgID, WoStatus, WoTextLC),
    )
  }

  override val primaryKey = PrimaryKey(WoID)
}
