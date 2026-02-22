package com.lute.application

import com.lute.dtos.BulkOperationResult
import com.lute.dtos.CreateTermDto
import com.lute.dtos.ImportResult
import com.lute.dtos.TermDto
import com.lute.dtos.UpdateTermDto

class TermServiceImpl(
    private val crudService: TermCrudService,
    private val bulkService: TermBulkService,
    private val csvService: TermCsvService,
    private val relationshipService: TermRelationshipService,
) : TermService {
  override fun getAllTerms(
      languageId: Long?,
      status: Int?,
      limit: Int,
      offset: Int,
  ): List<TermDto> = crudService.getAllTerms(languageId, status, limit, offset)

  override fun getTermById(id: Long): TermDto? = crudService.getTermById(id)

  override fun createTerm(dto: CreateTermDto): TermDto = crudService.createTerm(dto)

  override fun updateTerm(id: Long, dto: UpdateTermDto): TermDto? = crudService.updateTerm(id, dto)

  override fun deleteTerm(id: Long): Boolean = crudService.deleteTerm(id)

  override fun searchTerms(
      query: String,
      languageId: Long?,
      status: Int?,
  ): List<TermDto> = crudService.searchTerms(query, languageId, status)

  override fun bulkOperation(
      operation: String,
      termIds: List<Long>,
      status: Int?,
      tagIds: List<Long>?,
  ): BulkOperationResult = bulkService.bulkOperation(operation, termIds, status, tagIds)

  override fun addParent(termId: Long, parentId: Long) =
      relationshipService.addParent(termId, parentId)

  override fun removeParent(termId: Long, parentId: Long) =
      relationshipService.removeParent(termId, parentId)

  override fun getParents(termId: Long): List<TermDto> = relationshipService.getParents(termId)

  override fun exportToCsv(languageId: Long?, status: Int?): ByteArray =
      csvService.exportToCsv(languageId, status)

  override fun importFromCsv(csv: String, languageId: Long): ImportResult =
      csvService.importFromCsv(csv, languageId)
}
