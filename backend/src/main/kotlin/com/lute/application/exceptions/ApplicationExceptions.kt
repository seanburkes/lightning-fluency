package com.lute.application.exceptions

sealed class ApplicationException(message: String) : RuntimeException(message)

class EntityNotFoundException(entityType: String, identifier: Any) :
    ApplicationException("$entityType with ${formatIdentifier(identifier)} not found")

class DuplicateEntityException(entityType: String, identifier: String) :
    ApplicationException("$entityType '$identifier' already exists")

class EntityInUseException(entityType: String, reason: String) :
    ApplicationException("Cannot delete $entityType: $reason")

private fun formatIdentifier(identifier: Any): String =
    when (identifier) {
      is Long -> "id $identifier"
      is Int -> "id $identifier"
      is String -> "name '$identifier'"
      else -> identifier.toString()
    }
