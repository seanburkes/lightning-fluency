package com.lute.db.repositories

import com.lute.db.DatabaseTestBase
import com.lute.domain.Status
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class StatusRepositoryTest : DatabaseTestBase() {
  private val repo = StatusRepository()

  @Test
  fun `save returns generated id`() {
    val id = repo.save(Status(text = "New", abbreviation = "N"))
    assertTrue(id > 0)
  }

  @Test
  fun `findById returns saved status`() {
    val id = repo.save(Status(text = "Learning", abbreviation = "L"))
    val found = repo.findById(id)
    assertNotNull(found)
    assertEquals("Learning", found.text)
    assertEquals("L", found.abbreviation)
  }

  @Test
  fun `findById returns null for missing id`() {
    assertNull(repo.findById(999))
  }

  @Test
  fun `findAll returns all statuses`() {
    repo.save(Status(text = "New", abbreviation = "N"))
    repo.save(Status(text = "Known", abbreviation = "K"))
    repo.save(Status(text = "Ignored", abbreviation = "I"))
    assertEquals(3, repo.findAll().size)
  }
}
