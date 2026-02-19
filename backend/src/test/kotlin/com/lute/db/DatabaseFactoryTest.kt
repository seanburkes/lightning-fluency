package com.lute.db

import java.io.File
import kotlin.test.assertTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DatabaseFactoryTest {
  private val testDbPath = "/tmp/test_lute.db"

  @BeforeEach
  fun setup() {
    File(testDbPath).delete()
  }

  @AfterEach
  fun teardown() {
    DatabaseFactory.shutdown()
    File(testDbPath).delete()
  }

  @Test
  fun `test database initialization creates file`() {
    DatabaseFactory.init(testDbPath)

    assertTrue(File(testDbPath).exists(), "Database file should be created")
  }
}
