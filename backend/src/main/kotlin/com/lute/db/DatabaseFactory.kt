package com.lute.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
  private var dataSource: HikariDataSource? = null

  fun init(dbPath: String = System.getenv("LUTE_DB_PATH") ?: "/data/lute.db") {
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
    Database.connect(dataSource!!)
  }

  fun shutdown() {
    dataSource?.close()
  }
}
