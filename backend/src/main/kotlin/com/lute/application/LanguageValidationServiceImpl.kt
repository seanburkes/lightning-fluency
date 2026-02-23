package com.lute.application

import com.lute.db.repositories.LanguageRepository
import com.lute.parse.ParserFactory

class LanguageValidationServiceImpl(
    private val languageRepository: LanguageRepository,
    private val parserFactory: ParserFactory,
) : LanguageValidationService {
  override fun validateLanguageName(name: String, excludeId: Long?): ValidationResult {
    if (name.isBlank()) {
      return ValidationResult.Invalid("Language name is required")
    }

    if (name.length > 40) {
      return ValidationResult.Invalid("Language name must be 40 characters or less")
    }

    val existing = languageRepository.findByName(name)
    if (existing != null && existing.id != excludeId) {
      return ValidationResult.Invalid("Language with name '$name' already exists")
    }

    return ValidationResult.Valid
  }

  override fun validateParserType(parserType: String): Boolean {
    return parserType in parserFactory.supportedParserTypes()
  }

  override fun validateRegex(pattern: String?): Boolean {
    if (pattern.isNullOrBlank()) {
      return true
    }
    return try {
      Regex(pattern)
      true
    } catch (e: Exception) {
      false
    }
  }

  override fun validateLanguageNameRequired(name: String) {
    ValidationUtils.validator()
        .required("name", name, "Language name")
        .maxLength("name", name, 40, "Language name")
        .throwIfErrors()
  }

  override fun isDuplicateLanguageName(name: String, excludeId: Long?): Boolean {
    val existing = languageRepository.findByName(name)
    return existing != null && existing.id != excludeId
  }
}
