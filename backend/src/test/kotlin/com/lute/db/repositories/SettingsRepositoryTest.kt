package com.lute.db.repositories

import com.lute.db.DatabaseTestBase
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.jupiter.api.Test

class SettingsRepositoryTest : DatabaseTestBase() {
  private val repo = SettingsRepository()

  @Test
  fun `set and get roundtrip`() {
    repo.set("theme", "dark")
    assertEquals("dark", repo.get("theme"))
  }

  @Test
  fun `get returns null for missing key`() {
    assertNull(repo.get("nonexistent"))
  }

  @Test
  fun `set overwrites existing value`() {
    repo.set("lang", "en")
    repo.set("lang", "fr")
    assertEquals("fr", repo.get("lang"))
  }

  @Test
  fun `getAll returns all settings`() {
    repo.set("key1", "val1")
    repo.set("key2", "val2", keyType = "int")
    val all = repo.getAll()
    assertEquals(2, all.size)
    assertEquals("val1", all["key1"]?.value)
    assertEquals("val2", all["key2"]?.value)
    assertEquals("int", all["key2"]?.keyType)
  }
}
