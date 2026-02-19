package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object TextBookmarksTable : Table("textbookmarks") {
  val TbID = integer("TbID").autoIncrement()
  val TbTxID = integer("TbTxID").references(TextsTable.TxID)
  val TbTitle = text("TbTitle")

  override val primaryKey = PrimaryKey(TbID)
}
