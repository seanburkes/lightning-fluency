package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object MigrationsTable : Table("_migrations") {
  val migrationName = varchar("migration_name", 255)
  val appliedAt = long("applied_at")

  override val primaryKey = PrimaryKey(migrationName)
}
