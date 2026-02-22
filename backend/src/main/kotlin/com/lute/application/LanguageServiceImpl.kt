package com.lute.application

import com.lute.dtos.CreateLanguageDto
import com.lute.dtos.LanguageDto
import com.lute.dtos.UpdateLanguageDto

class LanguageServiceImpl(
    private val crudService: LanguageCrudService,
    private val validationService: LanguageValidationService,
) : LanguageService {
  override fun getAllLanguages(): List<LanguageDto> = crudService.getAllLanguages()

  override fun getLanguageById(id: Long): LanguageDto? = crudService.getLanguageById(id)

  override fun createLanguage(dto: CreateLanguageDto): LanguageDto = crudService.createLanguage(dto)

  override fun updateLanguage(id: Long, dto: UpdateLanguageDto): LanguageDto? =
      crudService.updateLanguage(id, dto)

  override fun deleteLanguage(id: Long) = crudService.deleteLanguage(id)

  override fun validateLanguageName(name: String, excludeId: Long?): ValidationResult =
      validationService.validateLanguageName(name, excludeId)

  override fun validateParserType(parserType: String): Boolean =
      validationService.validateParserType(parserType)

  override fun validateRegex(pattern: String?): Boolean = validationService.validateRegex(pattern)
}
