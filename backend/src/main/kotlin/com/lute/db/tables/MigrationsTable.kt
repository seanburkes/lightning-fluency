package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object MigrationsTable : Table("_migrations") {
  val MgName = varchar("mg_name", 255)
  val MgAppliedAt = long("mg_applied_at")
  val MgChecksum = varchar("mg_checksum", 64).nullable()

  override val primaryKey = PrimaryKey(MgName)
}
