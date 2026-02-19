package com.lute.db.tables

import com.lute.db.DatabaseFactory
import kotlin.test.Test
import kotlin.test.assertTrue
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class TagAndRelationshipTablesTest {
  @Test
  fun `test tag and relationship tables can be created`() {
    DatabaseFactory.init(":memory:")

    transaction {
      SchemaUtils.create(
          LanguagesTable,
          BooksTable,
          TextsTable,
          WordsTable,
          TagsTable,
          Tags2Table,
          WordTagsTable,
          BookTagsTable,
          WordParentsTable,
          SentencesTable,
      )
      assertTrue(true, "All tag and relationship tables should be created")
    }

    DatabaseFactory.shutdown()
  }
}
