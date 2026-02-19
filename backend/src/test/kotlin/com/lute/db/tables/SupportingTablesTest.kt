package com.lute.db.tables

import com.lute.db.DatabaseFactory
import kotlin.test.Test
import kotlin.test.assertTrue
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class SupportingTablesTest {
  @Test
  fun `test supporting tables can be created`() {
    DatabaseFactory.init(":memory:")

    transaction {
      SchemaUtils.create(
          LanguagesTable,
          BooksTable,
          WordsTable,
          SettingsTable,
          BookStatsTable,
          WordImagesTable,
          WordFlashMessagesTable,
          WordsReadTable,
          SrsExportSpecsTable,
          TextBookmarksTable,
          LanguageDictsTable,
          MigrationsTable,
      )
      assertTrue(true, "All supporting tables should be created")
    }

    DatabaseFactory.shutdown()
  }
}
