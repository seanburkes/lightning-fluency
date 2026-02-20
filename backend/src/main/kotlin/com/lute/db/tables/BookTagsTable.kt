package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object BookTagsTable : Table("booktags") {
  val BtBkID = long("BtBkID").references(BooksTable.BkID)
  val BtT2ID = long("BtT2ID").references(Tags2Table.T2ID)

  override val primaryKey = PrimaryKey(BtBkID, BtT2ID)
}
