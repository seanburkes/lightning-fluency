package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object SentencesTable : Table("sentences") {
  val SeID = long("SeID").autoIncrement()
  val SeTxID = long("SeTxID").references(TextsTable.TxID)
  val SeOrder = integer("SeOrder")
  val SeText = text("SeText").nullable()
  val SeTextLC = text("SeTextLC").nullable()

  init {
    index(customIndexName = "idx_sentences_txid_order", columns = arrayOf(SeTxID, SeOrder))
  }

  override val primaryKey = PrimaryKey(SeID)
}
