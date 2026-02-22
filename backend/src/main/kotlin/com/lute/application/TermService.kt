package com.lute.application

import com.lute.dtos.BulkOperationResult
import com.lute.dtos.CreateTermDto
import com.lute.dtos.ImportResult
import com.lute.dtos.TermDto
import com.lute.dtos.UpdateTermDto

interface TermService {
  fun getAllTerms(
      languageId: Long? = null,
      status: Int? = null,
      limit: Int = 100,
      offset: Int = 0,
  ): List<TermDto>

  fun getTermById(id: Long): TermDto?

  fun createTerm(dto: CreateTermDto): TermDto

  fun updateTerm(id: Long, dto: UpdateTermDto): TermDto?

  fun deleteTerm(id: Long): Boolean

  fun searchTerms(query: String, languageId: Long? = null, status: Int? = null): List<TermDto>

  fun bulkOperation(
      operation: String,
      termIds: List<Long>,
      status: Int? = null,
      tagIds: List<Long>? = null,
  ): BulkOperationResult

  fun addParent(termId: Long, parentId: Long)

  fun removeParent(termId: Long, parentId: Long)

  fun getParents(termId: Long): List<TermDto>

  fun exportToCsv(languageId: Long? = null, status: Int? = null): ByteArray

  fun importFromCsv(csv: String, languageId: Long): ImportResult
}
