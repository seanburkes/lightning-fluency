package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object StatusesTable : Table("statuses") {
  val StID = long("StID").autoIncrement()
  val StText = varchar("StText", 20)
  val StAbbreviation = varchar("StAbbreviation", 5)

  init {
    uniqueIndex("idx_statuses_text", StText)
    uniqueIndex("idx_statuses_abbreviation", StAbbreviation)
  }

  override val primaryKey = PrimaryKey(StID)
}
