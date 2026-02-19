package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object TagsTable : Table("tags") {
  val TgID = integer("TgID").autoIncrement()
  val TgText = varchar("TgText", 20)
  val TgComment = varchar("TgComment", 200)

  override val primaryKey = PrimaryKey(TgID)
}
