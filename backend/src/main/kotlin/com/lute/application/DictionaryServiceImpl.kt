package com.lute.application

import com.lute.db.repositories.DictionaryRepository
import com.lute.db.repositories.LanguageRepository
import com.lute.domain.Dictionary
import com.lute.dtos.CreateDictionaryDto
import com.lute.dtos.DictionaryDto
import com.lute.dtos.UpdateDictionaryDto

class DictionaryServiceImpl(
    private val dictionaryRepository: DictionaryRepository,
    private val languageRepository: LanguageRepository,
) : DictionaryService {
  override fun getDictionariesForLanguage(languageId: Long): List<DictionaryDto> {
    validateLanguageExists(languageId)
    return dictionaryRepository.findByLanguageId(languageId).map { it.toDto() }
  }

  override fun addDictionary(languageId: Long, dto: CreateDictionaryDto): DictionaryDto {
    validateLanguageExists(languageId)
    validateDictionaryDto(dto.ld_use_for, dto.ld_type, dto.ld_dict_uri)

    val dictionary =
        Dictionary(
            languageId = languageId,
            useFor = dto.ld_use_for,
            type = dto.ld_type,
            dictUri = dto.ld_dict_uri,
            isActive = dto.ld_is_active,
            sortOrder = dto.ld_sort_order,
        )

    val id = dictionaryRepository.save(dictionary)
    return dictionary.copy(id = id).toDto()
  }

  override fun updateDictionary(
      id: Long,
      languageId: Long,
      dto: UpdateDictionaryDto,
  ): DictionaryDto? {
    validateLanguageExists(languageId)
    val existing = dictionaryRepository.findByIdAndLanguageId(id, languageId) ?: return null

    val useFor = dto.ld_use_for ?: existing.useFor
    val type = dto.ld_type ?: existing.type
    val dictUri = dto.ld_dict_uri ?: existing.dictUri

    validateDictionaryDto(useFor, type, dictUri)

    val updated =
        existing.copy(
            useFor = useFor,
            type = type,
            dictUri = dictUri,
            isActive = dto.ld_is_active ?: existing.isActive,
            sortOrder = dto.ld_sort_order ?: existing.sortOrder,
        )

    dictionaryRepository.update(updated)
    return updated.toDto()
  }

  override fun deleteDictionary(id: Long, languageId: Long): Boolean {
    validateLanguageExists(languageId)
    val dictionary = dictionaryRepository.findByIdAndLanguageId(id, languageId) ?: return false
    dictionaryRepository.delete(dictionary.id)
    return true
  }

  override fun toggleDictionaryActive(id: Long, languageId: Long, active: Boolean): DictionaryDto? {
    validateLanguageExists(languageId)
    val existing = dictionaryRepository.findByIdAndLanguageId(id, languageId) ?: return null

    val updated = existing.copy(isActive = active)
    dictionaryRepository.update(updated)
    return updated.toDto()
  }

  private fun validateLanguageExists(languageId: Long) {
    languageRepository.require(languageId)
  }

  private fun validateDictionaryDto(useFor: String, type: String, dictUri: String) {
    ValidationUtils.validator()
        .required("ld_use_for", useFor, "Use for")
        .maxLength("ld_use_for", useFor, 20, "Use for")
        .required("ld_type", type, "Type")
        .maxLength("ld_type", type, 20, "Type")
        .required("ld_dict_uri", dictUri, "Dictionary URI")
        .maxLength("ld_dict_uri", dictUri, 200, "Dictionary URI")
        .throwIfErrors()
  }

  private fun Dictionary.toDto(): DictionaryDto {
    return DictionaryDto(
        id = id,
        language_id = languageId,
        ld_use_for = useFor,
        ld_type = type,
        ld_dict_uri = dictUri,
        ld_is_active = isActive,
        ld_sort_order = sortOrder,
    )
  }
}
