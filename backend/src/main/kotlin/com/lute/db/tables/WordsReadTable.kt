package com.lute.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object WordsReadTable : Table("wordsread") {
  val WrID = integer("WrID").autoIncrement()
  val WrLgID = integer("WrLgID").references(LanguagesTable.LgID)
  val WrTxID = integer("WrTxID").nullable()
  val WrReadDate = datetime("WrReadDate")
  val WrWordCount = integer("WrWordCount")

  override val primaryKey = PrimaryKey(WrID)
}
