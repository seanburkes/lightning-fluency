package com.lute.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
  private var dataSource: HikariDataSource? = null

  var database: Database? = null
    private set

  fun init(dbPath: String = System.getenv("LUTE_DB_PATH") ?: "/data/lute.db"): Database {
    val config =
        HikariConfig().apply {
          driverClassName = "org.sqlite.JDBC"
          jdbcUrl = "jdbc:sqlite:$dbPath"
          maximumPoolSize = 10
          minimumIdle = 2
          connectionTimeout = 30000
          connectionInitSql = "PRAGMA foreign_keys=ON"
        }

    dataSource = HikariDataSource(config)
    val db = Database.connect(dataSource!!)
    database = db
    return db
  }

  fun shutdown() {
    dataSource?.close()
    database = null
  }
}
