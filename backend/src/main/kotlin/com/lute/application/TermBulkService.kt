package com.lute.application

import com.lute.dtos.BulkOperationResult

interface TermBulkService {
  fun bulkOperation(
      operation: String,
      termIds: List<Long>,
      status: Int?,
      tagIds: List<Long>?,
  ): BulkOperationResult
}
