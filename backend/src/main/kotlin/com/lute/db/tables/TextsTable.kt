package com.lute.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object TextsTable : Table("texts") {
  val TxID = integer("TxID").autoIncrement()
  val TxBkID = integer("TxBkID").references(BooksTable.BkID)
  val TxOrder = integer("TxOrder")
  val TxText = text("TxText")
  val TxReadDate = datetime("TxReadDate").nullable()
  val TxWordCount = integer("TxWordCount").nullable()
  val TxStartDate = datetime("TxStartDate").nullable()

  override val primaryKey = PrimaryKey(TxID)
}
