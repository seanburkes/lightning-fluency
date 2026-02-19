package com.lute.db.migrations

import com.lute.db.tables.MigrationsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class MigrationManager(private val db: Database) {
  private val logger = LoggerFactory.getLogger(MigrationManager::class.java)

  companion object {
    private const val MIGRATIONS_PATH = "db/migrations"
    private const val REPEATABLE_PATH = "db/repeatable"
  }

  fun runMigrations() {
    transaction(db) { SchemaUtils.create(MigrationsTable) }

    val applied = getAppliedMigrations()
    val migrations = loadMigrationsFromResources(MIGRATIONS_PATH)

    for ((name, sql) in migrations) {
      if (name in applied) {
        logger.info("Skipping already-applied migration: {}", name)
        continue
      }
      logger.info("Applying migration: {}", name)
      applyMigration(name, sql)
    }

    val repeatables = loadMigrationsFromResources(REPEATABLE_PATH)
    for ((name, sql) in repeatables) {
      logger.info("Applying repeatable migration: {}", name)
      transaction(db) { execStatements(sql) }
    }
  }

  fun getAppliedMigrations(): Set<String> =
      transaction(db) {
        MigrationsTable.selectAll().map { it[MigrationsTable.migrationName] }.toSet()
      }

  fun applyMigration(name: String, sql: String) {
    transaction(db) {
      execStatements(sql)
      MigrationsTable.insert {
        it[migrationName] = name
        it[appliedAt] = System.currentTimeMillis()
      }
    }
  }

  private fun org.jetbrains.exposed.sql.Transaction.execStatements(sql: String) {
    splitStatements(sql).forEach { exec(it) }
  }

  private fun splitStatements(sql: String): List<String> {
    val statements = mutableListOf<String>()
    val current = StringBuilder()
    var insideBlock = false

    for (line in sql.lines()) {
      val trimmed = line.trim()
      if (trimmed.isEmpty() || trimmed.startsWith("--")) continue

      current.append(line).append("\n")

      if (trimmed.uppercase().startsWith("BEGIN")) insideBlock = true

      if (trimmed.endsWith(";") && !insideBlock) {
        statements.add(current.toString().trim())
        current.clear()
      } else if (insideBlock && trimmed.uppercase().startsWith("END;")) {
        insideBlock = false
        statements.add(current.toString().trim())
        current.clear()
      }
    }

    val remaining = current.toString().trim()
    if (remaining.isNotEmpty()) statements.add(remaining)

    return statements
  }

  fun loadMigrationsFromResources(path: String): List<Pair<String, String>> {
    val classLoader = Thread.currentThread().contextClassLoader
    val dirUrl = classLoader.getResource(path) ?: return emptyList()

    return dirUrl
        .openStream()
        .bufferedReader()
        .readLines()
        .filter { it.endsWith(".sql") }
        .sorted()
        .map { fileName ->
          val sql =
              classLoader.getResourceAsStream("$path/$fileName")?.bufferedReader()?.readText()
                  ?: throw IllegalStateException("Migration file not found: $path/$fileName")
          fileName.removeSuffix(".sql") to sql
        }
  }
}
