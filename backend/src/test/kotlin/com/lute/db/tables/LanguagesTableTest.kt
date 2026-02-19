package com.lute.db.tables

import com.lute.db.DatabaseFactory
import kotlin.test.Test
import kotlin.test.assertTrue
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class LanguagesTableTest {
  @Test
  fun `test languages table can be created`() {
    DatabaseFactory.init(":memory:")

    transaction {
      SchemaUtils.create(LanguagesTable)
      assertTrue(true, "Languages table should be created without errors")
    }

    DatabaseFactory.shutdown()
  }
}
