package com.lute.db.migrations

import com.lute.db.DatabaseFactory
import com.lute.db.tables.SettingsTable
import com.lute.db.tables.StatusesTable
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MigrationManagerTest {
  private lateinit var manager: MigrationManager

  @BeforeEach
  fun setUp() {
    val db = DatabaseFactory.init(":memory:")
    manager = MigrationManager(db)
  }

  @AfterEach
  fun tearDown() {
    DatabaseFactory.shutdown()
  }

  @Test
  fun `runMigrations creates all tables`() {
    manager.runMigrations()

    val tables = transaction {
      exec("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'") {
        val result = mutableListOf<String>()
        while (it.next()) result.add(it.getString(1))
        result
      } ?: emptyList()
    }

    val expected =
        listOf(
            "languages",
            "books",
            "texts",
            "words",
            "statuses",
            "tags",
            "tags2",
            "wordtags",
            "booktags",
            "wordparents",
            "sentences",
            "settings",
            "bookstats",
            "wordimages",
            "wordflashmessages",
            "wordsread",
            "srsexportspecs",
            "textbookmarks",
            "languagedicts",
            "_migrations",
        )

    for (table in expected) {
      assertTrue(tables.contains(table), "Expected table '$table' to exist, found: $tables")
    }
  }

  @Test
  fun `runMigrations inserts seed statuses`() {
    manager.runMigrations()

    val count = transaction { StatusesTable.selectAll().count() }
    assertEquals(8, count)
  }

  @Test
  fun `runMigrations inserts seed settings`() {
    manager.runMigrations()

    val count = transaction { SettingsTable.selectAll().count() }
    assertEquals(8, count)
  }

  @Test
  fun `runMigrations records applied migration`() {
    manager.runMigrations()

    val applied = manager.getAppliedMigrations()
    assertTrue(applied.contains("V1__initial_schema"), "V1 migration should be recorded")
  }

  @Test
  fun `runMigrations skips already applied migrations`() {
    manager.runMigrations()
    manager.runMigrations()

    val applied = manager.getAppliedMigrations()
    assertEquals(1, applied.size, "Should have exactly 1 migration recorded after running twice")
  }

  @Test
  fun `runMigrations creates indexes`() {
    manager.runMigrations()

    val indexes = transaction {
      exec("SELECT name FROM sqlite_master WHERE type='index' AND name LIKE 'idx_%'") {
        val result = mutableListOf<String>()
        while (it.next()) result.add(it.getString(1))
        result
      } ?: emptyList()
    }

    assertTrue(indexes.contains("idx_languages_name"), "Expected idx_languages_name")
    assertTrue(indexes.contains("idx_words_textlc_lgid"), "Expected idx_words_textlc_lgid")
    assertTrue(indexes.contains("idx_books_lgid"), "Expected idx_books_lgid")
  }

  @Test
  fun `runMigrations applies triggers`() {
    manager.runMigrations()

    val triggers = transaction {
      exec("SELECT name FROM sqlite_master WHERE type='trigger'") {
        val result = mutableListOf<String>()
        while (it.next()) result.add(it.getString(1))
        result
      } ?: emptyList()
    }

    assertTrue(
        triggers.contains("trig_words_update_status_children"),
        "Expected children sync trigger",
    )
    assertTrue(
        triggers.contains("trig_words_update_status_parent"),
        "Expected parent sync trigger",
    )
  }
}
