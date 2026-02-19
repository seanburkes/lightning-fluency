package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object SentencesTable : Table("sentences") {
  val SeID = integer("SeID").autoIncrement()
  val SeTxID = integer("SeTxID").references(TextsTable.TxID)
  val SeOrder = integer("SeOrder")
  val SeText = text("SeText").nullable()
  val SeTextLC = text("SeTextLC").nullable()

  override val primaryKey = PrimaryKey(SeID)
}
