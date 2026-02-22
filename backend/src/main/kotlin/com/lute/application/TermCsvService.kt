package com.lute.application

import com.lute.dtos.ImportResult

interface TermCsvService {
  fun exportToCsv(languageId: Long?, status: Int?): ByteArray

  fun importFromCsv(csv: String, languageId: Long): ImportResult
}
