package com.lute.db

import com.lute.db.migrations.MigrationManager
import com.lute.db.tables.BooksTable
import com.lute.db.tables.LanguagesTable
import com.lute.db.tables.TextsTable
import com.lute.db.tables.WordParentsTable
import com.lute.db.tables.WordsTable
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DatabaseIntegrationTest {
  @BeforeEach
  fun setUp() {
    val db = DatabaseFactory.init(":memory:")
    MigrationManager(db).runMigrations()
  }

  @AfterEach
  fun tearDown() {
    DatabaseFactory.shutdown()
  }

  @Test
  fun `foreign key constraint prevents book creation with nonexistent language`() {
    assertFailsWith<ExposedSQLException> {
      transaction {
        BooksTable.insert {
          it[BkLgID] = 9999
          it[BkTitle] = "Orphaned Book"
        }
      }
    }
  }

  @Test
  fun `foreign key constraint prevents text creation with nonexistent book`() {
    assertFailsWith<ExposedSQLException> {
      transaction {
        TextsTable.insert {
          it[TxBkID] = 9999
          it[TxOrder] = 1
          it[TxText] = "Orphaned text"
        }
      }
    }
  }

  @Test
  fun `foreign key constraint prevents word creation with nonexistent language`() {
    assertFailsWith<ExposedSQLException> {
      transaction {
        WordsTable.insert {
          it[WoLgID] = 9999
          it[WoText] = "orphan"
          it[WoTextLC] = "orphan"
        }
      }
    }
  }

  @Test
  fun `trigger propagates parent status change to children`() {
    transaction {
      val lgId = LanguagesTable.insert { it[LgName] = "Spanish" } get LanguagesTable.LgID

      val parentId =
          WordsTable.insert {
            it[WoLgID] = lgId
            it[WoText] = "correr"
            it[WoTextLC] = "correr"
            it[WoStatus] = 1
            it[WoSyncStatus] = 1
          } get WordsTable.WoID

      val childId =
          WordsTable.insert {
            it[WoLgID] = lgId
            it[WoText] = "corriendo"
            it[WoTextLC] = "corriendo"
            it[WoStatus] = 1
          } get WordsTable.WoID

      WordParentsTable.insert {
        it[WpWoID] = childId
        it[WpParentWoID] = parentId
      }

      WordsTable.update({ WordsTable.WoID eq parentId }) { it[WoStatus] = 3 }

      val childStatus =
          WordsTable.selectAll().where { WordsTable.WoID eq childId }.single()[WordsTable.WoStatus]
      assertEquals(3, childStatus, "Child status should sync to parent's new status")
    }
  }

  @Test
  fun `trigger propagates child status change to sync-enabled parent`() {
    transaction {
      val lgId = LanguagesTable.insert { it[LgName] = "Spanish" } get LanguagesTable.LgID

      val parentId =
          WordsTable.insert {
            it[WoLgID] = lgId
            it[WoText] = "leer"
            it[WoTextLC] = "leer"
            it[WoStatus] = 1
            it[WoSyncStatus] = 1
          } get WordsTable.WoID

      val childId =
          WordsTable.insert {
            it[WoLgID] = lgId
            it[WoText] = "leyendo"
            it[WoTextLC] = "leyendo"
            it[WoStatus] = 1
          } get WordsTable.WoID

      WordParentsTable.insert {
        it[WpWoID] = childId
        it[WpParentWoID] = parentId
      }

      WordsTable.update({ WordsTable.WoID eq childId }) { it[WoStatus] = 5 }

      val parentStatus =
          WordsTable.selectAll().where { WordsTable.WoID eq parentId }.single()[WordsTable.WoStatus]
      assertEquals(5, parentStatus, "Parent status should sync to child's new status")
    }
  }

  @Test
  fun `trigger does not propagate to parent when sync is disabled`() {
    transaction {
      val lgId = LanguagesTable.insert { it[LgName] = "Spanish" } get LanguagesTable.LgID

      val parentId =
          WordsTable.insert {
            it[WoLgID] = lgId
            it[WoText] = "hablar"
            it[WoTextLC] = "hablar"
            it[WoStatus] = 1
            it[WoSyncStatus] = 0
          } get WordsTable.WoID

      val childId =
          WordsTable.insert {
            it[WoLgID] = lgId
            it[WoText] = "hablando"
            it[WoTextLC] = "hablando"
            it[WoStatus] = 1
          } get WordsTable.WoID

      WordParentsTable.insert {
        it[WpWoID] = childId
        it[WpParentWoID] = parentId
      }

      WordsTable.update({ WordsTable.WoID eq childId }) { it[WoStatus] = 4 }

      val parentStatus =
          WordsTable.selectAll().where { WordsTable.WoID eq parentId }.single()[WordsTable.WoStatus]
      assertEquals(1, parentStatus, "Parent status should NOT change when sync is disabled")
    }
  }

  @Test
  fun `end-to-end creates language, book, text, and terms`() {
    transaction {
      val lgId =
          LanguagesTable.insert {
            it[LgName] = "Japanese"
            it[LgParserType] = "mecab"
          } get LanguagesTable.LgID

      val bookId =
          BooksTable.insert {
            it[BkLgID] = lgId
            it[BkTitle] = "Kitchen"
          } get BooksTable.BkID

      val textId =
          TextsTable.insert {
            it[TxBkID] = bookId
            it[TxOrder] = 1
            it[TxText] = "私がこの世でいちばん好きな場所は台所だと思う。"
          } get TextsTable.TxID

      val termId =
          WordsTable.insert {
            it[WoLgID] = lgId
            it[WoText] = "台所"
            it[WoTextLC] = "台所"
            it[WoStatus] = 1
            it[WoTranslation] = "kitchen"
          } get WordsTable.WoID

      assertTrue(lgId > 0)
      assertTrue(bookId > 0)
      assertTrue(textId > 0)
      assertTrue(termId > 0)

      assertEquals(1, LanguagesTable.selectAll().count())
      assertEquals(1, BooksTable.selectAll().count())
      assertEquals(1, TextsTable.selectAll().count())
      assertEquals(1, WordsTable.selectAll().count())
    }
  }

  @Test
  fun `deleting a language cascading via manual cleanup`() {
    transaction {
      val lgId = LanguagesTable.insert { it[LgName] = "French" } get LanguagesTable.LgID

      val bookId =
          BooksTable.insert {
            it[BkLgID] = lgId
            it[BkTitle] = "Le Petit Prince"
          } get BooksTable.BkID

      TextsTable.insert {
        it[TxBkID] = bookId
        it[TxOrder] = 1
        it[TxText] = "Lorsque j'avais six ans..."
      }

      WordsTable.insert {
        it[WoLgID] = lgId
        it[WoText] = "prince"
        it[WoTextLC] = "prince"
      }

      WordsTable.deleteWhere { WoLgID eq lgId }
      TextsTable.deleteWhere { TxBkID eq bookId }
      BooksTable.deleteWhere { BkID eq bookId }
      LanguagesTable.deleteWhere { LgID eq lgId }

      assertEquals(0, LanguagesTable.selectAll().count())
      assertEquals(0, BooksTable.selectAll().count())
      assertEquals(0, TextsTable.selectAll().count())
      assertEquals(0, WordsTable.selectAll().count())
    }
  }
}
