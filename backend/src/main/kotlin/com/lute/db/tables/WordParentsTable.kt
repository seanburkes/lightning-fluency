package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object WordParentsTable : Table("wordparents") {
  val WpWoID = long("WpWoID").references(WordsTable.WoID)
  val WpParentWoID = long("WpParentWoID").references(WordsTable.WoID)

  init {
    index(customIndexName = "idx_wordparents_woid", columns = arrayOf(WpWoID))
    index(customIndexName = "idx_wordparents_parent_woid", columns = arrayOf(WpParentWoID))
  }

  override val primaryKey = PrimaryKey(WpWoID, WpParentWoID)
}
