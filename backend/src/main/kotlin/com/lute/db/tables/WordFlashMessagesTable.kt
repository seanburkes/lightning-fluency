package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object WordFlashMessagesTable : Table("wordflashmessages") {
  val WfID = integer("WfID").autoIncrement()
  val WfWoID = integer("WfWoID").references(WordsTable.WoID)
  val WfMessage = varchar("WfMessage", 200)

  override val primaryKey = PrimaryKey(WfID)
}
