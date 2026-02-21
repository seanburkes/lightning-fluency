package com.lute.services

import com.lute.application.LanguageServiceImpl
import com.lute.application.ValidationResult
import com.lute.application.exceptions.DuplicateLanguageException
import com.lute.application.exceptions.LanguageNotFoundException
import com.lute.application.exceptions.ValidationException
import com.lute.db.repositories.LanguageRepository
import com.lute.domain.Language
import com.lute.dtos.CreateLanguageDto
import com.lute.dtos.UpdateLanguageDto
import com.lute.parse.ParserFactory
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

class LanguageServiceTest {
  private val languageRepository = mockk<LanguageRepository>(relaxed = true)
  private val parserFactory = ParserFactory()
  private val languageService = LanguageServiceImpl(languageRepository, parserFactory)

  @Test
  fun `getAllLanguages returns all languages`() {
    val languages =
        listOf(
            Language(id = 1, name = "English", parserType = "spacedel"),
            Language(id = 2, name = "Spanish", parserType = "spacedel"),
        )
    every { languageRepository.findAll() } returns languages

    val result = languageService.getAllLanguages()

    assertEquals(2, result.size)
    assertEquals("English", result[0].name)
    assertEquals("Spanish", result[1].name)
  }

  @Test
  fun `getAllLanguages returns empty list when no languages`() {
    every { languageRepository.findAll() } returns emptyList()

    val result = languageService.getAllLanguages()

    assertTrue(result.isEmpty())
  }

  @Test
  fun `getLanguageById returns correct language`() {
    val language = Language(id = 1, name = "English", parserType = "spacedel")
    every { languageRepository.findById(1) } returns language

    val result = languageService.getLanguageById(1)

    assertNotNull(result)
    assertEquals("English", result.name)
    assertEquals("spacedel", result.parser_type)
  }

  @Test
  fun `getLanguageById returns null for non-existent language`() {
    every { languageRepository.findById(999) } returns null

    val result = languageService.getLanguageById(999)

    assertNull(result)
  }

  @Test
  fun `createLanguage with valid data`() {
    val dto = CreateLanguageDto(name = "French", parser_type = "spacedel")
    every { languageRepository.findByName("French") } returns null
    every { languageRepository.save(any()) } returns 1

    val result = languageService.createLanguage(dto)

    assertEquals("French", result.name)
    assertEquals("spacedel", result.parser_type)
    verify { languageRepository.save(any()) }
  }

  @Test
  fun `createLanguage with duplicate name throws exception`() {
    val dto = CreateLanguageDto(name = "English", parser_type = "spacedel")
    val existing = Language(id = 1, name = "English", parserType = "spacedel")
    every { languageRepository.findByName("English") } returns existing

    assertFailsWith<DuplicateLanguageException> { languageService.createLanguage(dto) }
  }

  @Test
  fun `createLanguage with invalid parser type throws exception`() {
    val dto = CreateLanguageDto(name = "Test", parser_type = "invalid_parser")
    every { languageRepository.findByName("Test") } returns null

    assertFailsWith<ValidationException> { languageService.createLanguage(dto) }
  }

  @Test
  fun `createLanguage with invalid regex throws exception`() {
    val dto =
        CreateLanguageDto(
            name = "Test",
            parser_type = "spacedel",
            regexp_split_sentences = "[invalid",
        )
    every { languageRepository.findByName("Test") } returns null

    assertFailsWith<ValidationException> { languageService.createLanguage(dto) }
  }

  @Test
  fun `updateLanguage with valid data`() {
    val existing = Language(id = 1, name = "English", parserType = "spacedel")
    val dto = UpdateLanguageDto(name = "British English")
    every { languageRepository.findById(1) } returns existing
    every { languageRepository.findByName("British English") } returns null
    every { languageRepository.update(any()) } returns Unit

    val result = languageService.updateLanguage(1, dto)

    assertNotNull(result)
    assertEquals("British English", result.name)
    verify { languageRepository.update(any()) }
  }

  @Test
  fun `updateLanguage with duplicate name throws exception`() {
    val existing = Language(id = 1, name = "English", parserType = "spacedel")
    val other = Language(id = 2, name = "Spanish", parserType = "spacedel")
    val dto = UpdateLanguageDto(name = "Spanish")
    every { languageRepository.findById(1) } returns existing
    every { languageRepository.findByName("Spanish") } returns other

    assertFailsWith<DuplicateLanguageException> { languageService.updateLanguage(1, dto) }
  }

  @Test
  fun `updateLanguage returns null for non-existent language`() {
    every { languageRepository.findById(999) } returns null
    val dto = UpdateLanguageDto(name = "Test")

    val result = languageService.updateLanguage(999, dto)

    assertNull(result)
  }

  @Test
  fun `deleteLanguage throws exception for non-existent language`() {
    every { languageRepository.findById(999) } returns null

    assertFailsWith<LanguageNotFoundException> { languageService.deleteLanguage(999) }
  }

  @Test
  fun `validateLanguageName returns valid for unique name`() {
    every { languageRepository.findByName("Unique") } returns null

    val result = languageService.validateLanguageName("Unique")

    assertTrue(result is ValidationResult.Valid)
  }

  @Test
  fun `validateLanguageName returns invalid for blank name`() {
    val result = languageService.validateLanguageName("")

    assertTrue(result is ValidationResult.Invalid)
  }

  @Test
  fun `validateLanguageName returns invalid for name too long`() {
    val longName = "a".repeat(41)
    val result = languageService.validateLanguageName(longName)

    assertTrue(result is ValidationResult.Invalid)
  }

  @Test
  fun `validateParserType returns true for valid parser types`() {
    assertTrue(languageService.validateParserType("spacedel"))
    assertTrue(languageService.validateParserType("turkish"))
  }

  @Test
  fun `validateParserType returns false for unimplemented parser types`() {
    assertFalse(languageService.validateParserType("japanese"))
    assertFalse(languageService.validateParserType("chinese"))
    assertFalse(languageService.validateParserType("korean"))
  }

  @Test
  fun `validateParserType returns false for invalid parser type`() {
    assertFalse(languageService.validateParserType("invalid"))
    assertFalse(languageService.validateParserType(""))
  }

  @Test
  fun `validateRegex returns true for valid regex`() {
    assertTrue(languageService.validateRegex("[a-zA-Z]+"))
    assertTrue(languageService.validateRegex("[.?!]+"))
  }

  @Test
  fun `validateRegex returns true for null or blank`() {
    assertTrue(languageService.validateRegex(null))
    assertTrue(languageService.validateRegex(""))
    assertTrue(languageService.validateRegex("   "))
  }

  @Test
  fun `validateRegex returns false for invalid regex`() {
    assertFalse(languageService.validateRegex("[invalid"))
    assertFalse(languageService.validateRegex("("))
  }
}
