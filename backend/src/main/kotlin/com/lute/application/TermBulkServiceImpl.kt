package com.lute.application

import com.lute.db.repositories.TagRepository
import com.lute.db.repositories.TermRepository
import com.lute.dtos.BulkOperationResult

class TermBulkServiceImpl(
    private val termRepository: TermRepository,
    private val tagRepository: TagRepository,
) : TermBulkService {
  override fun bulkOperation(
      operation: String,
      termIds: List<Long>,
      status: Int?,
      tagIds: List<Long>?,
  ): BulkOperationResult {
    var updated = 0
    var failed = 0
    val errors = mutableListOf<String>()

    when (operation) {
      "update_status" -> {
        if (status == null) {
          return BulkOperationResult(0, 0, listOf("status is required for update_status"))
        }
        termIds.forEach { termId ->
          try {
            val term = termRepository.findById(termId)
            if (term != null) {
              termRepository.update(term.copy(status = status))
              updated++
            } else {
              failed++
              errors.add("Term $termId not found")
            }
          } catch (e: Exception) {
            failed++
            errors.add("Term $termId: ${e.message}")
          }
        }
      }
      "add_tags" -> {
        if (tagIds == null) {
          return BulkOperationResult(0, 0, listOf("tag_ids are required for add_tags"))
        }
        termIds.forEach { termId ->
          try {
            val term = termRepository.findById(termId)
            if (term != null) {
              tagIds.forEach { tagId -> tagRepository.addTagToTerm(termId, tagId) }
              updated++
            } else {
              failed++
              errors.add("Term $termId not found")
            }
          } catch (e: Exception) {
            failed++
            errors.add("Term $termId: ${e.message}")
          }
        }
      }
      "remove_tags" -> {
        if (tagIds == null) {
          return BulkOperationResult(0, 0, listOf("tag_ids are required for remove_tags"))
        }
        termIds.forEach { termId ->
          try {
            val term = termRepository.findById(termId)
            if (term != null) {
              tagIds.forEach { tagId -> tagRepository.removeTagFromTerm(termId, tagId) }
              updated++
            } else {
              failed++
              errors.add("Term $termId not found")
            }
          } catch (e: Exception) {
            failed++
            errors.add("Term $termId: ${e.message}")
          }
        }
      }
      else -> return BulkOperationResult(0, 0, listOf("Unknown operation: $operation"))
    }

    return BulkOperationResult(updated, failed, errors)
  }
}
