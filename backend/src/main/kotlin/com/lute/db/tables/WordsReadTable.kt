package com.lute.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object WordsReadTable : Table("wordsread") {
  val WrID = long("WrID").autoIncrement()
  val WrLgID = long("WrLgID").references(LanguagesTable.LgID)
  val WrTxID = long("WrTxID").nullable()
  val WrReadDate = datetime("WrReadDate")
  val WrWordCount = integer("WrWordCount")

  init {
    index(customIndexName = "idx_wordsread_lgid", columns = arrayOf(WrLgID))
    index(customIndexName = "idx_wordsread_txid", columns = arrayOf(WrTxID))
  }

  override val primaryKey = PrimaryKey(WrID)
}
