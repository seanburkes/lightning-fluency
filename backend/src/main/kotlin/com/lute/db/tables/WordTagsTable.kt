package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object WordTagsTable : Table("wordtags") {
  val WtWoID = long("WtWoID").references(WordsTable.WoID)
  val WtTgID = long("WtTgID").references(TagsTable.TgID)

  override val primaryKey = PrimaryKey(WtWoID, WtTgID)
}
