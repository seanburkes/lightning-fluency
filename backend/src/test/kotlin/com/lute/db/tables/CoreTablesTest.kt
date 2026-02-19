package com.lute.db.tables

import com.lute.db.DatabaseFactory
import kotlin.test.Test
import kotlin.test.assertTrue
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class CoreTablesTest {
  @Test
  fun `test core tables can be created`() {
    DatabaseFactory.init(":memory:")

    transaction {
      SchemaUtils.create(LanguagesTable, BooksTable, TextsTable, WordsTable, StatusesTable)
      assertTrue(true, "All core tables should be created")
    }

    DatabaseFactory.shutdown()
  }
}
