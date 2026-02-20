package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object WordImagesTable : Table("wordimages") {
  val WiID = long("WiID").autoIncrement()
  val WiWoID = long("WiWoID").references(WordsTable.WoID)
  val WiSource = varchar("WiSource", 500)

  init {
    index(customIndexName = "idx_wordimages_woid", columns = arrayOf(WiWoID))
  }

  override val primaryKey = PrimaryKey(WiID)
}
