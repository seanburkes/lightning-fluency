package com.lute.application

import com.lute.application.exceptions.EntityNotFoundException
import com.lute.db.repositories.LanguageRepository
import com.lute.db.repositories.TagRepository
import com.lute.db.repositories.TermRepository
import com.lute.domain.Term
import com.lute.dtos.ImportResult

class TermCsvServiceImpl(
    private val termRepository: TermRepository,
    private val languageRepository: LanguageRepository,
    private val tagRepository: TagRepository,
) : TermCsvService {
  override fun exportToCsv(languageId: Long?, status: Int?): ByteArray {
    val terms = termRepository.findAll(languageId, status, Int.MAX_VALUE, 0)
    val tagsMap = tagRepository.getTagsForTerms(terms.map { it.id })

    val header = "text,translation,romanization,status,tags\n"
    val rows =
        terms.joinToString("\n") { term ->
          val tags = tagsMap[term.id]?.joinToString(";") { it.text } ?: ""
          val escapedText = term.text.replace("\"", "\"\"")
          val escapedTranslation = (term.translation ?: "").replace("\"", "\"\"")
          val escapedRomanization = (term.romanization ?: "").replace("\"", "\"\"")
          val escapedTags = tags.replace("\"", "\"\"")
          "\"$escapedText\",\"$escapedTranslation\",\"$escapedRomanization\",${term.status},\"$escapedTags\""
        }
    return (header + rows).toByteArray()
  }

  override fun importFromCsv(csv: String, languageId: Long): ImportResult {
    languageRepository.findById(languageId) ?: throw EntityNotFoundException("Language", languageId)

    var imported = 0
    var skipped = 0
    val errors = mutableListOf<String>()

    val lines = csv.lines().filter { it.isNotBlank() }
    if (lines.isEmpty()) {
      return ImportResult(0, 0, listOf("Empty CSV"))
    }

    val dataLines = if (lines.first().lowercase().contains("text")) lines.drop(1) else lines

    dataLines.forEachIndexed { index, line ->
      try {
        val parts = parseCsvLine(line)
        if (parts.isEmpty()) {
          skipped++
          return@forEachIndexed
        }

        val text = parts.getOrNull(0) ?: ""
        if (text.isBlank()) {
          skipped++
          return@forEachIndexed
        }

        val existing = termRepository.findByTextAndLanguage(text.lowercase(), languageId)
        if (existing != null) {
          skipped++
          return@forEachIndexed
        }

        val term =
            Term(
                languageId = languageId,
                text = text,
                textLC = text.lowercase(),
                translation = parts.getOrNull(1)?.ifBlank { null },
                romanization = parts.getOrNull(2)?.ifBlank { null },
                status = parts.getOrNull(3)?.toIntOrNull() ?: 0,
                tokenCount = text.split(Regex("\\s+")).size,
            )

        termRepository.save(term)
        imported++
      } catch (e: Exception) {
        errors.add("Line ${index + 1}: ${e.message}")
      }
    }

    return ImportResult(imported, skipped, errors)
  }

  private fun parseCsvLine(line: String): List<String> {
    val result = mutableListOf<String>()
    var current = StringBuilder()
    var inQuotes = false

    for (char in line) {
      when {
        char == '"' -> inQuotes = !inQuotes
        char == ',' && !inQuotes -> {
          result.add(current.toString().trim())
          current = StringBuilder()
        }
        else -> current.append(char)
      }
    }
    result.add(current.toString().trim())

    return result
  }
}
