package com.lute.application.exceptions

class LanguageNotFoundException(message: String) : RuntimeException(message)

class LanguageInUseException(message: String) : RuntimeException(message)

class DuplicateLanguageException(message: String) : RuntimeException(message)

class ValidationException(val errors: List<Pair<String, String>>) :
    RuntimeException("Validation failed")
