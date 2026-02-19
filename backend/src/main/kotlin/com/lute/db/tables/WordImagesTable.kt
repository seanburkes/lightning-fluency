package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object WordImagesTable : Table("wordimages") {
  val WiID = integer("WiID").autoIncrement()
  val WiWoID = integer("WiWoID").references(WordsTable.WoID)
  val WiSource = varchar("WiSource", 500)

  override val primaryKey = PrimaryKey(WiID)
}
