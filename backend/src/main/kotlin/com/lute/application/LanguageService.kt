package com.lute.application

import com.lute.dtos.CreateLanguageDto
import com.lute.dtos.LanguageDto
import com.lute.dtos.UpdateLanguageDto

interface LanguageService {
  fun getAllLanguages(): List<LanguageDto>

  fun getLanguageById(id: Long): LanguageDto?

  fun createLanguage(dto: CreateLanguageDto): LanguageDto

  fun updateLanguage(id: Long, dto: UpdateLanguageDto): LanguageDto?

  fun deleteLanguage(id: Long)

  fun validateLanguageName(name: String, excludeId: Long? = null): ValidationResult

  fun validateParserType(parserType: String): Boolean

  fun validateRegex(pattern: String?): Boolean
}

sealed class ValidationResult {
  data object Valid : ValidationResult()

  data class Invalid(val message: String) : ValidationResult()
}
