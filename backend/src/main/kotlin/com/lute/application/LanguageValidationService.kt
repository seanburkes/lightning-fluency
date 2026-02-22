package com.lute.application

sealed class ValidationResult {
  data object Valid : ValidationResult()

  data class Invalid(val message: String) : ValidationResult()
}

interface LanguageValidationService {
  fun validateLanguageName(name: String, excludeId: Long? = null): ValidationResult

  fun validateParserType(parserType: String): Boolean

  fun validateRegex(pattern: String?): Boolean

  fun validateLanguageNameRequired(name: String)

  fun isDuplicateLanguageName(name: String, excludeId: Long?): Boolean
}
