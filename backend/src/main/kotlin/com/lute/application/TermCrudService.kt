package com.lute.application

import com.lute.dtos.CreateTermDto
import com.lute.dtos.TermDto
import com.lute.dtos.UpdateTermDto

interface TermCrudService {
  fun getAllTerms(languageId: Long?, status: Int?, limit: Int, offset: Int): List<TermDto>

  fun getTermById(id: Long): TermDto?

  fun createTerm(dto: CreateTermDto): TermDto

  fun updateTerm(id: Long, dto: UpdateTermDto): TermDto?

  fun deleteTerm(id: Long): Boolean

  fun searchTerms(query: String, languageId: Long?, status: Int?): List<TermDto>
}
