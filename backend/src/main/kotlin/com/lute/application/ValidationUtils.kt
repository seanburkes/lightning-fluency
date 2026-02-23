package com.lute.application

import com.lute.application.exceptions.ValidationException

object ValidationUtils {
  class Validator {
    private val errors = mutableListOf<Pair<String, String>>()

    fun required(field: String, value: String?, name: String): Validator {
      if (value.isNullOrBlank()) {
        errors.add(field to "$name is required")
      }
      return this
    }

    fun maxLength(field: String, value: String?, max: Int, name: String): Validator {
      if (value != null && value.length > max) {
        errors.add(field to "$name must be $max characters or less")
      }
      return this
    }

    fun regex(field: String, pattern: String?, name: String): Validator {
      if (!pattern.isNullOrBlank()) {
        try {
          Regex(pattern)
        } catch (e: Exception) {
          errors.add(field to "$name must be a valid regular expression")
        }
      }
      return this
    }

    fun custom(field: String, condition: Boolean, message: String): Validator {
      if (!condition) {
        errors.add(field to message)
      }
      return this
    }

    fun hasErrors(): Boolean = errors.isNotEmpty()

    fun getErrors(): List<Pair<String, String>> = errors.toList()

    fun throwIfErrors() {
      if (errors.isNotEmpty()) {
        throw ValidationException(errors.toList())
      }
    }
  }

  fun validator() = Validator()
}
