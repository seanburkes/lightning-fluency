package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object TextBookmarksTable : Table("textbookmarks") {
  val TbID = long("TbID").autoIncrement()
  val TbTxID = long("TbTxID").references(TextsTable.TxID)
  val TbTitle = text("TbTitle")

  init {
    index(customIndexName = "idx_textbookmarks_txid", columns = arrayOf(TbTxID))
  }

  override val primaryKey = PrimaryKey(TbID)
}
