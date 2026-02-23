package com.lute.application

import com.lute.application.exceptions.ValidationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidationUtilsTest {
  @Test
  fun `required adds error for null value`() {
    val validator = ValidationUtils.validator()
    validator.required("name", null, "Name")
    assertTrue(validator.hasErrors())
    assertEquals(1, validator.getErrors().size)
    assertEquals("name" to "Name is required", validator.getErrors().first())
  }

  @Test
  fun `required adds error for blank value`() {
    val validator = ValidationUtils.validator()
    validator.required("name", "", "Name")
    assertTrue(validator.hasErrors())
  }

  @Test
  fun `required does not add error for valid value`() {
    val validator = ValidationUtils.validator()
    validator.required("name", "English", "Name")
    assertFalse(validator.hasErrors())
  }

  @Test
  fun `maxLength adds error when exceeded`() {
    val validator = ValidationUtils.validator()
    validator.maxLength("name", "This is a very long name", 10, "Name")
    assertTrue(validator.hasErrors())
    assertEquals("name" to "Name must be 10 characters or less", validator.getErrors().first())
  }

  @Test
  fun `maxLength does not add error when within limit`() {
    val validator = ValidationUtils.validator()
    validator.maxLength("name", "Short", 10, "Name")
    assertFalse(validator.hasErrors())
  }

  @Test
  fun `maxLength does not add error for null value`() {
    val validator = ValidationUtils.validator()
    validator.maxLength("name", null, 10, "Name")
    assertFalse(validator.hasErrors())
  }

  @Test
  fun `regex adds error for invalid pattern`() {
    val validator = ValidationUtils.validator()
    validator.regex("pattern", "[invalid", "Pattern")
    assertTrue(validator.hasErrors())
    assertEquals(
        "pattern" to "Pattern must be a valid regular expression",
        validator.getErrors().first(),
    )
  }

  @Test
  fun `regex does not add error for valid pattern`() {
    val validator = ValidationUtils.validator()
    validator.regex("pattern", "[a-z]+", "Pattern")
    assertFalse(validator.hasErrors())
  }

  @Test
  fun `regex does not add error for null pattern`() {
    val validator = ValidationUtils.validator()
    validator.regex("pattern", null, "Pattern")
    assertFalse(validator.hasErrors())
  }

  @Test
  fun `regex does not add error for blank pattern`() {
    val validator = ValidationUtils.validator()
    validator.regex("pattern", "", "Pattern")
    assertFalse(validator.hasErrors())
  }

  @Test
  fun `custom adds error when condition is false`() {
    val validator = ValidationUtils.validator()
    validator.custom("field", false, "Custom validation failed")
    assertTrue(validator.hasErrors())
    assertEquals("field" to "Custom validation failed", validator.getErrors().first())
  }

  @Test
  fun `custom does not add error when condition is true`() {
    val validator = ValidationUtils.validator()
    validator.custom("field", true, "Custom validation failed")
    assertFalse(validator.hasErrors())
  }

  @Test
  fun `chained validations accumulate errors`() {
    val validator = ValidationUtils.validator()
    validator.required("name", null, "Name").maxLength("name", "x".repeat(50), 10, "Name")

    assertTrue(validator.hasErrors())
    assertEquals(2, validator.getErrors().size)
  }

  @Test
  fun `throwIfErrors throws ValidationException with errors`() {
    val validator = ValidationUtils.validator()
    validator.required("name", null, "Name")

    var exceptionThrown = false
    try {
      validator.throwIfErrors()
    } catch (e: ValidationException) {
      exceptionThrown = true
      assertEquals(1, e.errors.size)
      assertEquals("name" to "Name is required", e.errors.first())
    }
    assertTrue(exceptionThrown)
  }

  @Test
  fun `throwIfErrors does not throw when no errors`() {
    val validator = ValidationUtils.validator()
    validator.required("name", "Valid", "Name")

    validator.throwIfErrors()
  }

  @Test
  fun `fluent API works correctly`() {
    var exceptionThrown = false
    try {
      ValidationUtils.validator()
          .required("name", "", "Name")
          .maxLength("name", "x".repeat(50), 10, "Name")
          .throwIfErrors()
    } catch (e: ValidationException) {
      exceptionThrown = true
      assertEquals(2, e.errors.size)
    }
    assertTrue(exceptionThrown)
  }
}
