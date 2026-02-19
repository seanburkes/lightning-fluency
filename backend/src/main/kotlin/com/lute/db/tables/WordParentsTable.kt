package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object WordParentsTable : Table("wordparents") {
  val WpWoID = integer("WpWoID").references(WordsTable.WoID)
  val WpParentWoID = integer("WpParentWoID").references(WordsTable.WoID)

  override val primaryKey = PrimaryKey(WpWoID, WpParentWoID)
}
