package com.lute.application

import com.lute.dtos.CreateLanguageDto
import com.lute.dtos.LanguageDto
import com.lute.dtos.UpdateLanguageDto

interface LanguageCrudService {
  fun getAllLanguages(): List<LanguageDto>

  fun getLanguageById(id: Long): LanguageDto?

  fun createLanguage(dto: CreateLanguageDto): LanguageDto

  fun updateLanguage(id: Long, dto: UpdateLanguageDto): LanguageDto?

  fun deleteLanguage(id: Long)
}
