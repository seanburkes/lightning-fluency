package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object Tags2Table : Table("tags2") {
  val T2ID = long("T2ID").autoIncrement()
  val T2Text = varchar("T2Text", 20)
  val T2Comment = varchar("T2Comment", 200)

  init {
    uniqueIndex("idx_tags2_text", T2Text)
  }

  override val primaryKey = PrimaryKey(T2ID)
}
