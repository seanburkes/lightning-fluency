package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object WordTagsTable : Table("wordtags") {
  val WtWoID = integer("WtWoID").references(WordsTable.WoID)
  val WtTgID = integer("WtTgID").references(TagsTable.TgID)

  override val primaryKey = PrimaryKey(WtWoID, WtTgID)
}
