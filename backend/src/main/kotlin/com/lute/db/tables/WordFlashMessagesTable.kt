package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object WordFlashMessagesTable : Table("wordflashmessages") {
  val WfID = long("WfID").autoIncrement()
  val WfWoID = long("WfWoID").references(WordsTable.WoID)
  val WfMessage = varchar("WfMessage", 200)

  init {
    index(customIndexName = "idx_wordflashmessages_woid", columns = arrayOf(WfWoID))
  }

  override val primaryKey = PrimaryKey(WfID)
}
