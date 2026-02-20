package com.lute.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object TextsTable : Table("texts") {
  val TxID = long("TxID").autoIncrement()
  val TxBkID = long("TxBkID").references(BooksTable.BkID)
  val TxOrder = integer("TxOrder")
  val TxText = text("TxText")
  val TxReadDate = datetime("TxReadDate").nullable()
  val TxWordCount = integer("TxWordCount").nullable()
  val TxStartDate = datetime("TxStartDate").nullable()

  init {
    index(customIndexName = "idx_texts_book_order", columns = arrayOf(TxBkID, TxOrder))
  }

  override val primaryKey = PrimaryKey(TxID)
}
