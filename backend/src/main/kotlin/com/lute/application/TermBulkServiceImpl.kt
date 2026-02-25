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
    when (operation) {
      "update_status" -> {
        if (status == null) {
          return BulkOperationResult(0, 0, listOf("status is required for update_status"))
        }
        if (termIds.isEmpty()) {
          return BulkOperationResult(0, 0, emptyList())
        }
        val updated = termRepository.updateStatus(termIds, status)
        return BulkOperationResult(updated, 0, emptyList())
      }
      "add_tags" -> {
        if (tagIds == null) {
          return BulkOperationResult(0, 0, listOf("tag_ids are required for add_tags"))
        }
        if (termIds.isEmpty() || tagIds.isEmpty()) {
          return BulkOperationResult(0, 0, emptyList())
        }
        val updated = tagRepository.addTagsToTerms(termIds, tagIds)
        return BulkOperationResult(updated, 0, emptyList())
      }
      "remove_tags" -> {
        if (tagIds == null) {
          return BulkOperationResult(0, 0, listOf("tag_ids are required for remove_tags"))
        }
        if (termIds.isEmpty() || tagIds.isEmpty()) {
          return BulkOperationResult(0, 0, emptyList())
        }
        val removed = tagRepository.removeTagsFromTerms(termIds, tagIds)
        return BulkOperationResult(removed, 0, emptyList())
      }
      else -> return BulkOperationResult(0, 0, listOf("Unknown operation: $operation"))
    }
  }
}
