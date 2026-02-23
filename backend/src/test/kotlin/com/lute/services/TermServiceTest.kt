package com.lute.services

import com.lute.application.TermBulkService
import com.lute.application.TermCrudService
import com.lute.application.TermCsvService
import com.lute.application.TermRelationshipService
import com.lute.application.TermServiceImpl
import com.lute.application.exceptions.DuplicateEntityException
import com.lute.application.exceptions.EntityNotFoundException
import com.lute.dtos.BulkOperationResult
import com.lute.dtos.CreateTermDto
import com.lute.dtos.ImportResult
import com.lute.dtos.TermDto
import com.lute.dtos.UpdateTermDto
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TermServiceTest {
  private val crudService = mockk<TermCrudService>(relaxed = true)
  private val bulkService = mockk<TermBulkService>(relaxed = true)
  private val csvService = mockk<TermCsvService>(relaxed = true)
  private val relationshipService = mockk<TermRelationshipService>(relaxed = true)
  private val termService =
      TermServiceImpl(crudService, bulkService, csvService, relationshipService)

  @Test
  fun `getAllTerms returns terms from crud service`() {
    val terms =
        listOf(
            TermDto(id = 1, text = "hello", language_id = 1, status = 0),
            TermDto(id = 2, text = "world", language_id = 1, status = 1),
        )
    every { crudService.getAllTerms(null, null, 100, 0) } returns terms

    val result = termService.getAllTerms()

    assertEquals(2, result.size)
    assertEquals("hello", result[0].text)
    assertEquals("world", result[1].text)
  }

  @Test
  fun `getAllTerms with filters passes to crud service`() {
    val terms = listOf(TermDto(id = 1, text = "hello", language_id = 1, status = 1))
    every { crudService.getAllTerms(1L, 1, 50, 10) } returns terms

    val result = termService.getAllTerms(languageId = 1L, status = 1, limit = 50, offset = 10)

    assertEquals(1, result.size)
    verify { crudService.getAllTerms(1L, 1, 50, 10) }
  }

  @Test
  fun `getTermById returns term from crud service`() {
    val term = TermDto(id = 1, text = "hello", language_id = 1, status = 0)
    every { crudService.getTermById(1L) } returns term

    val result = termService.getTermById(1L)

    assertNotNull(result)
    assertEquals("hello", result.text)
  }

  @Test
  fun `getTermById returns null for non-existent term`() {
    every { crudService.getTermById(999L) } returns null

    val result = termService.getTermById(999L)

    assertNull(result)
  }

  @Test
  fun `createTerm delegates to crud service`() {
    val dto = CreateTermDto(text = "test", language_id = 1L)
    val created = TermDto(id = 1, text = "test", language_id = 1L, status = 0)
    every { crudService.createTerm(dto) } returns created

    val result = termService.createTerm(dto)

    assertEquals(1L, result.id)
    assertEquals("test", result.text)
    verify { crudService.createTerm(dto) }
  }

  @Test
  fun `createTerm propagates DuplicateEntityException`() {
    val dto = CreateTermDto(text = "duplicate", language_id = 1L)
    every { crudService.createTerm(dto) } throws DuplicateEntityException("Term", "duplicate")

    assertFailsWith<DuplicateEntityException> { termService.createTerm(dto) }
  }

  @Test
  fun `updateTerm delegates to crud service`() {
    val dto = UpdateTermDto(status = 3)
    val updated = TermDto(id = 1, text = "hello", language_id = 1L, status = 3)
    every { crudService.updateTerm(1L, dto) } returns updated

    val result = termService.updateTerm(1L, dto)

    assertNotNull(result)
    assertEquals(3, result.status)
  }

  @Test
  fun `updateTerm returns null for non-existent term`() {
    val dto = UpdateTermDto(status = 3)
    every { crudService.updateTerm(999L, dto) } returns null

    val result = termService.updateTerm(999L, dto)

    assertNull(result)
  }

  @Test
  fun `deleteTerm delegates to crud service`() {
    every { crudService.deleteTerm(1L) } returns true

    val result = termService.deleteTerm(1L)

    assertTrue(result)
    verify { crudService.deleteTerm(1L) }
  }

  @Test
  fun `deleteTerm returns false for non-existent term`() {
    every { crudService.deleteTerm(999L) } returns false

    val result = termService.deleteTerm(999L)

    assertFalse(result)
  }

  @Test
  fun `searchTerms delegates to crud service`() {
    val terms = listOf(TermDto(id = 1, text = "hello", language_id = 1L, status = 0))
    every { crudService.searchTerms("hel", 1L, null) } returns terms

    val result = termService.searchTerms("hel", languageId = 1L)

    assertEquals(1, result.size)
    assertEquals("hello", result[0].text)
  }

  @Test
  fun `bulkOperation delegates to bulk service`() {
    val expectedResult = BulkOperationResult(updated = 3, failed = 0)
    every { bulkService.bulkOperation("update_status", listOf(1L, 2L, 3L), 1, null) } returns
        expectedResult

    val result = termService.bulkOperation("update_status", listOf(1L, 2L, 3L), status = 1)

    assertEquals(3, result.updated)
    assertEquals(0, result.failed)
  }

  @Test
  fun `bulkOperation with add_tags delegates to bulk service`() {
    val expectedResult = BulkOperationResult(updated = 2, failed = 0)
    every { bulkService.bulkOperation("add_tags", listOf(1L, 2L), null, listOf(10L)) } returns
        expectedResult

    val result = termService.bulkOperation("add_tags", listOf(1L, 2L), tagIds = listOf(10L))

    assertEquals(2, result.updated)
  }

  @Test
  fun `bulkOperation with remove_tags delegates to bulk service`() {
    val expectedResult = BulkOperationResult(updated = 1, failed = 0)
    every { bulkService.bulkOperation("remove_tags", listOf(1L), null, listOf(10L, 11L)) } returns
        expectedResult

    val result = termService.bulkOperation("remove_tags", listOf(1L), tagIds = listOf(10L, 11L))

    assertEquals(1, result.updated)
  }

  @Test
  fun `addParent delegates to relationship service`() {
    every { relationshipService.addParent(1L, 2L) } returns Unit

    termService.addParent(1L, 2L)

    verify { relationshipService.addParent(1L, 2L) }
  }

  @Test
  fun `addParent propagates EntityNotFoundException`() {
    every { relationshipService.addParent(999L, 1L) } throws EntityNotFoundException("Term", 999L)

    assertFailsWith<EntityNotFoundException> { termService.addParent(999L, 1L) }
  }

  @Test
  fun `removeParent delegates to relationship service`() {
    every { relationshipService.removeParent(1L, 2L) } returns Unit

    termService.removeParent(1L, 2L)

    verify { relationshipService.removeParent(1L, 2L) }
  }

  @Test
  fun `getParents delegates to relationship service`() {
    val parents = listOf(TermDto(id = 2, text = "parent", language_id = 1L, status = 5))
    every { relationshipService.getParents(1L) } returns parents

    val result = termService.getParents(1L)

    assertEquals(1, result.size)
    assertEquals("parent", result[0].text)
  }

  @Test
  fun `getParents propagates EntityNotFoundException`() {
    every { relationshipService.getParents(999L) } throws EntityNotFoundException("Term", 999L)

    assertFailsWith<EntityNotFoundException> { termService.getParents(999L) }
  }

  @Test
  fun `exportToCsv delegates to csv service`() {
    val csvBytes = "text,translation\nhello,hola\n".toByteArray()
    every { csvService.exportToCsv(1L, null) } returns csvBytes

    val result = termService.exportToCsv(languageId = 1L)

    assertTrue(result.isNotEmpty())
    assertTrue(String(result).contains("hello"))
  }

  @Test
  fun `exportToCsv with status filter delegates to csv service`() {
    val csvBytes = "text,translation\n".toByteArray()
    every { csvService.exportToCsv(1L, 5) } returns csvBytes

    val result = termService.exportToCsv(languageId = 1L, status = 5)

    verify { csvService.exportToCsv(1L, 5) }
  }

  @Test
  fun `importFromCsv delegates to csv service`() {
    val csv = "text,translation\nhello,hola"
    val expectedResult = ImportResult(imported = 1, skipped = 0)
    every { csvService.importFromCsv(csv, 1L) } returns expectedResult

    val result = termService.importFromCsv(csv, 1L)

    assertEquals(1, result.imported)
    assertEquals(0, result.skipped)
  }

  @Test
  fun `importFromCsv with errors returns result with errors`() {
    val csv = "text\n"
    val expectedResult = ImportResult(imported = 0, skipped = 0, errors = listOf("Empty CSV"))
    every { csvService.importFromCsv(csv, 1L) } returns expectedResult

    val result = termService.importFromCsv(csv, 1L)

    assertEquals(1, result.errors.size)
    assertEquals("Empty CSV", result.errors[0])
  }
}
