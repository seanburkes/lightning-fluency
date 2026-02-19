package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object StatusesTable : Table("statuses") {
  val StID = integer("StID").autoIncrement()
  val StText = varchar("StText", 20)
  val StAbbreviation = varchar("StAbbreviation", 5)

  override val primaryKey = PrimaryKey(StID)
}
