package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object SettingsTable : Table("settings") {
  val StKey = varchar("StKey", 40)
  val StKeyType = text("StKeyType")
  val StValue = text("StValue").nullable()

  override val primaryKey = PrimaryKey(StKey)
}
