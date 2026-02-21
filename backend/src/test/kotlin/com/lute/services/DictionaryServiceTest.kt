package com.lute.services

import com.lute.application.DictionaryServiceImpl
import com.lute.application.exceptions.LanguageNotFoundException
import com.lute.application.exceptions.ValidationException
import com.lute.db.repositories.DictionaryRepository
import com.lute.db.repositories.LanguageRepository
import com.lute.domain.Dictionary
import com.lute.domain.Language
import com.lute.dtos.CreateDictionaryDto
import com.lute.dtos.UpdateDictionaryDto
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

class DictionaryServiceTest {
  private val dictionaryRepository = mockk<DictionaryRepository>(relaxed = true)
  private val languageRepository = mockk<LanguageRepository>(relaxed = true)
  private val dictionaryService = DictionaryServiceImpl(dictionaryRepository, languageRepository)

  @Test
  fun `getDictionariesForLanguage returns dictionaries`() {
    val dictionaries =
        listOf(
            Dictionary(
                id = 1,
                languageId = 1,
                useFor = "terms",
                type = "url",
                dictUri = "https://dict.com",
                isActive = true,
                sortOrder = 1,
            ),
            Dictionary(
                id = 2,
                languageId = 1,
                useFor = "sentences",
                type = "url",
                dictUri = "https://sentences.com",
                isActive = false,
                sortOrder = 2,
            ),
        )
    every { languageRepository.findById(1) } returns
        Language(id = 1, name = "English", parserType = "spacedel")
    every { dictionaryRepository.findByLanguageId(1) } returns dictionaries

    val result = dictionaryService.getDictionariesForLanguage(1)

    assertEquals(2, result.size)
    assertEquals("terms", result[0].ld_use_for)
    assertEquals("sentences", result[1].ld_use_for)
  }

  @Test
  fun `getDictionariesForLanguage throws exception for non-existent language`() {
    every { languageRepository.findById(999) } returns null

    assertFailsWith<LanguageNotFoundException> { dictionaryService.getDictionariesForLanguage(999) }
  }

  @Test
  fun `addDictionary creates new dictionary`() {
    val dto =
        CreateDictionaryDto(
            ld_use_for = "terms",
            ld_type = "url",
            ld_dict_uri = "https://dict.com",
            ld_sort_order = 1,
            ld_is_active = true,
        )
    every { languageRepository.findById(1) } returns
        Language(id = 1, name = "English", parserType = "spacedel")
    every { dictionaryRepository.save(any()) } returns 1

    val result = dictionaryService.addDictionary(1, dto)

    assertEquals("terms", result.ld_use_for)
    assertEquals("https://dict.com", result.ld_dict_uri)
    assertEquals(1, result.ld_sort_order)
    verify { dictionaryRepository.save(any()) }
  }

  @Test
  fun `addDictionary throws exception for non-existent language`() {
    val dto =
        CreateDictionaryDto(
            ld_use_for = "terms",
            ld_type = "url",
            ld_dict_uri = "https://dict.com",
            ld_sort_order = 1,
        )
    every { languageRepository.findById(999) } returns null

    assertFailsWith<LanguageNotFoundException> { dictionaryService.addDictionary(999, dto) }
  }

  @Test
  fun `updateDictionary updates existing dictionary`() {
    val existing =
        Dictionary(
            id = 1,
            languageId = 1,
            useFor = "terms",
            type = "url",
            dictUri = "https://old.com",
            isActive = true,
            sortOrder = 1,
        )
    val dto = UpdateDictionaryDto(ld_dict_uri = "https://new.com")
    every { languageRepository.findById(1) } returns
        Language(id = 1, name = "English", parserType = "spacedel")
    every { dictionaryRepository.findByIdAndLanguageId(1, 1) } returns existing
    every { dictionaryRepository.update(any()) } returns Unit

    val result = dictionaryService.updateDictionary(1, 1, dto)

    assertNotNull(result)
    assertEquals("https://new.com", result.ld_dict_uri)
    assertEquals("terms", result.ld_use_for)
    verify { dictionaryRepository.update(any()) }
  }

  @Test
  fun `updateDictionary returns null for non-existent dictionary`() {
    every { languageRepository.findById(1) } returns
        Language(id = 1, name = "English", parserType = "spacedel")
    every { dictionaryRepository.findByIdAndLanguageId(999, 1) } returns null
    val dto = UpdateDictionaryDto(ld_dict_uri = "https://new.com")

    val result = dictionaryService.updateDictionary(999, 1, dto)

    assertNull(result)
  }

  @Test
  fun `updateDictionary returns null when dictionary belongs to different language`() {
    every { languageRepository.findById(2) } returns
        Language(id = 2, name = "Spanish", parserType = "spacedel")
    every { dictionaryRepository.findByIdAndLanguageId(1, 2) } returns null
    val dto = UpdateDictionaryDto(ld_dict_uri = "https://new.com")

    val result = dictionaryService.updateDictionary(1, 2, dto)

    assertNull(result)
  }

  @Test
  fun `updateDictionary throws exception for non-existent language`() {
    every { languageRepository.findById(999) } returns null
    val dto = UpdateDictionaryDto(ld_dict_uri = "https://new.com")

    assertFailsWith<LanguageNotFoundException> { dictionaryService.updateDictionary(1, 999, dto) }
  }

  @Test
  fun `deleteDictionary removes dictionary`() {
    val dictionary =
        Dictionary(
            id = 1,
            languageId = 1,
            useFor = "terms",
            type = "url",
            dictUri = "https://dict.com",
            isActive = true,
            sortOrder = 1,
        )
    every { languageRepository.findById(1) } returns
        Language(id = 1, name = "English", parserType = "spacedel")
    every { dictionaryRepository.findByIdAndLanguageId(1, 1) } returns dictionary
    every { dictionaryRepository.delete(1) } returns Unit

    val result = dictionaryService.deleteDictionary(1, 1)

    assertTrue(result)
    verify { dictionaryRepository.delete(1) }
  }

  @Test
  fun `deleteDictionary returns false for non-existent dictionary`() {
    every { languageRepository.findById(1) } returns
        Language(id = 1, name = "English", parserType = "spacedel")
    every { dictionaryRepository.findByIdAndLanguageId(999, 1) } returns null

    val result = dictionaryService.deleteDictionary(999, 1)

    assertFalse(result)
  }

  @Test
  fun `deleteDictionary returns false when dictionary belongs to different language`() {
    every { languageRepository.findById(2) } returns
        Language(id = 2, name = "Spanish", parserType = "spacedel")
    every { dictionaryRepository.findByIdAndLanguageId(1, 2) } returns null

    val result = dictionaryService.deleteDictionary(1, 2)

    assertFalse(result)
  }

  @Test
  fun `deleteDictionary throws exception for non-existent language`() {
    every { languageRepository.findById(999) } returns null

    assertFailsWith<LanguageNotFoundException> { dictionaryService.deleteDictionary(1, 999) }
  }

  @Test
  fun `toggleDictionaryActive changes active status to false`() {
    val existing =
        Dictionary(
            id = 1,
            languageId = 1,
            useFor = "terms",
            type = "url",
            dictUri = "https://dict.com",
            isActive = true,
            sortOrder = 1,
        )
    every { languageRepository.findById(1) } returns
        Language(id = 1, name = "English", parserType = "spacedel")
    every { dictionaryRepository.findByIdAndLanguageId(1, 1) } returns existing
    every { dictionaryRepository.update(any()) } returns Unit

    val result = dictionaryService.toggleDictionaryActive(1, 1, false)

    assertNotNull(result)
    assertFalse(result.ld_is_active)
  }

  @Test
  fun `toggleDictionaryActive changes active status to true`() {
    val existing =
        Dictionary(
            id = 1,
            languageId = 1,
            useFor = "terms",
            type = "url",
            dictUri = "https://dict.com",
            isActive = false,
            sortOrder = 1,
        )
    every { languageRepository.findById(1) } returns
        Language(id = 1, name = "English", parserType = "spacedel")
    every { dictionaryRepository.findByIdAndLanguageId(1, 1) } returns existing
    every { dictionaryRepository.update(any()) } returns Unit

    val result = dictionaryService.toggleDictionaryActive(1, 1, true)

    assertNotNull(result)
    assertTrue(result.ld_is_active)
  }

  @Test
  fun `toggleDictionaryActive returns null for non-existent dictionary`() {
    every { languageRepository.findById(1) } returns
        Language(id = 1, name = "English", parserType = "spacedel")
    every { dictionaryRepository.findByIdAndLanguageId(999, 1) } returns null

    val result = dictionaryService.toggleDictionaryActive(999, 1, true)

    assertNull(result)
  }

  @Test
  fun `toggleDictionaryActive throws exception for non-existent language`() {
    every { languageRepository.findById(999) } returns null

    assertFailsWith<LanguageNotFoundException> {
      dictionaryService.toggleDictionaryActive(1, 999, true)
    }
  }

  @Test
  fun `addDictionary throws exception for blank use_for`() {
    val dto =
        CreateDictionaryDto(
            ld_use_for = "",
            ld_type = "url",
            ld_dict_uri = "https://dict.com",
            ld_sort_order = 1,
        )
    every { languageRepository.findById(1) } returns
        Language(id = 1, name = "English", parserType = "spacedel")

    assertFailsWith<ValidationException> { dictionaryService.addDictionary(1, dto) }
  }

  @Test
  fun `addDictionary throws exception for blank type`() {
    val dto =
        CreateDictionaryDto(
            ld_use_for = "terms",
            ld_type = "",
            ld_dict_uri = "https://dict.com",
            ld_sort_order = 1,
        )
    every { languageRepository.findById(1) } returns
        Language(id = 1, name = "English", parserType = "spacedel")

    assertFailsWith<ValidationException> { dictionaryService.addDictionary(1, dto) }
  }

  @Test
  fun `addDictionary throws exception for blank dict_uri`() {
    val dto =
        CreateDictionaryDto(
            ld_use_for = "terms",
            ld_type = "url",
            ld_dict_uri = "",
            ld_sort_order = 1,
        )
    every { languageRepository.findById(1) } returns
        Language(id = 1, name = "English", parserType = "spacedel")

    assertFailsWith<ValidationException> { dictionaryService.addDictionary(1, dto) }
  }

  @Test
  fun `addDictionary throws exception for use_for exceeding max length`() {
    val dto =
        CreateDictionaryDto(
            ld_use_for = "a".repeat(21),
            ld_type = "url",
            ld_dict_uri = "https://dict.com",
            ld_sort_order = 1,
        )
    every { languageRepository.findById(1) } returns
        Language(id = 1, name = "English", parserType = "spacedel")

    assertFailsWith<ValidationException> { dictionaryService.addDictionary(1, dto) }
  }

  @Test
  fun `addDictionary throws exception for dict_uri exceeding max length`() {
    val dto =
        CreateDictionaryDto(
            ld_use_for = "terms",
            ld_type = "url",
            ld_dict_uri = "a".repeat(201),
            ld_sort_order = 1,
        )
    every { languageRepository.findById(1) } returns
        Language(id = 1, name = "English", parserType = "spacedel")

    assertFailsWith<ValidationException> { dictionaryService.addDictionary(1, dto) }
  }
}
