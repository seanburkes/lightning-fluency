package com.lute.db

import com.lute.db.tables.Tables
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/** Base class for repository tests using an in-memory SQLite database. */
abstract class DatabaseTestBase {
  @BeforeEach
  fun setUpDatabase() {
    DatabaseFactory.init(":memory:")
    transaction { SchemaUtils.create(*Tables.allTables) }
  }

  @AfterEach
  fun tearDownDatabase() {
    DatabaseFactory.shutdown()
  }
}
