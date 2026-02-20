package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object TagsTable : Table("tags") {
  val TgID = long("TgID").autoIncrement()
  val TgText = varchar("TgText", 20)
  val TgComment = varchar("TgComment", 200)

  init {
    uniqueIndex("idx_tags_text", TgText)
  }

  override val primaryKey = PrimaryKey(TgID)
}
