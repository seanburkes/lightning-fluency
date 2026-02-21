package com.lute.application

import com.lute.dtos.CreateDictionaryDto
import com.lute.dtos.DictionaryDto
import com.lute.dtos.UpdateDictionaryDto

interface DictionaryService {
  fun getDictionariesForLanguage(languageId: Long): List<DictionaryDto>

  fun addDictionary(languageId: Long, dto: CreateDictionaryDto): DictionaryDto

  fun updateDictionary(id: Long, languageId: Long, dto: UpdateDictionaryDto): DictionaryDto?

  fun deleteDictionary(id: Long, languageId: Long): Boolean

  fun toggleDictionaryActive(id: Long, languageId: Long, active: Boolean): DictionaryDto?
}
