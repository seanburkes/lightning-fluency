package com.lute.application

import com.lute.application.exceptions.DuplicateTermException
import com.lute.application.exceptions.LanguageNotFoundException
import com.lute.db.repositories.LanguageRepository
import com.lute.db.repositories.TagRepository
import com.lute.db.repositories.TermRepository
import com.lute.domain.Term
import com.lute.dtos.CreateTermDto
import com.lute.dtos.TermDto
import com.lute.dtos.UpdateTermDto
import java.time.format.DateTimeFormatter

class TermCrudServiceImpl(
    private val termRepository: TermRepository,
    private val languageRepository: LanguageRepository,
    private val tagRepository: TagRepository,
) : TermCrudService {
  private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  override fun getAllTerms(
      languageId: Long?,
      status: Int?,
      limit: Int,
      offset: Int,
  ): List<TermDto> {
    val terms = termRepository.findAll(languageId, status, limit, offset)
    return termsToDtos(terms)
  }

  override fun getTermById(id: Long): TermDto? {
    val term = termRepository.findById(id) ?: return null
    return termToDto(term)
  }

  override fun createTerm(dto: CreateTermDto): TermDto {
    languageRepository.findById(dto.language_id)
        ?: throw LanguageNotFoundException("Language with id ${dto.language_id} not found")

    val existing = termRepository.findByTextAndLanguage(dto.text.lowercase(), dto.language_id)
    if (existing != null) {
      throw DuplicateTermException("Term '${dto.text}' already exists for this language")
    }

    val term =
        Term(
            languageId = dto.language_id,
            text = dto.text,
            textLC = dto.text.lowercase(),
            status = dto.status,
            translation = dto.translation,
            romanization = dto.romanization,
            tokenCount = dto.text.split(Regex("\\s+")).size,
        )

    val id = termRepository.save(term)
    val savedTerm = term.copy(id = id)

    dto.tags.forEach { tagId -> tagRepository.addTagToTerm(id, tagId) }

    return termToDto(savedTerm)
  }

  override fun updateTerm(id: Long, dto: UpdateTermDto): TermDto? {
    val existing = termRepository.findById(id) ?: return null

    if (dto.text != null) {
      val duplicate =
          termRepository.findByTextAndLanguage(dto.text.lowercase(), existing.languageId)
      if (duplicate != null && duplicate.id != id) {
        throw DuplicateTermException("Term '${dto.text}' already exists for this language")
      }
    }

    val updated =
        existing.copy(
            text = dto.text ?: existing.text,
            textLC = dto.text?.lowercase() ?: existing.textLC,
            translation = dto.translation ?: existing.translation,
            romanization = dto.romanization ?: existing.romanization,
            status = dto.status ?: existing.status,
        )

    termRepository.update(updated)
    return termToDto(updated)
  }

  override fun deleteTerm(id: Long): Boolean {
    termRepository.findById(id) ?: return false
    termRepository.deleteWithRelationships(id)
    return true
  }

  override fun searchTerms(
      query: String,
      languageId: Long?,
      status: Int?,
  ): List<TermDto> {
    val terms = termRepository.findByTextContaining(query, languageId, status)
    return termsToDtos(terms)
  }

  private fun termsToDtos(terms: List<Term>): List<TermDto> {
    if (terms.isEmpty()) return emptyList()

    val termIds = terms.map { it.id }
    val tagsMap = tagRepository.getTagsForTerms(termIds)
    val parentsMap = termRepository.getParentIdsForTerms(termIds)
    val childrenCountMap = termRepository.getChildrenCountForTerms(termIds)

    val parentIds = parentsMap.values.flatten().distinct()
    val parentsTextMap =
        if (parentIds.isNotEmpty()) {
          val parents = termRepository.findByIds(parentIds)
          parents.associate { it.id to it.text }
        } else {
          emptyMap()
        }

    return terms.map { term ->
      val tagTexts = tagsMap[term.id]?.map { it.text } ?: emptyList()
      val parentIdsList = parentsMap[term.id] ?: emptyList()
      val parentTexts = parentIdsList.mapNotNull { parentsTextMap[it] }
      val childrenCount = childrenCountMap[term.id] ?: 0

      TermDto(
          id = term.id,
          text = term.text,
          language_id = term.languageId,
          status = term.status,
          translation = term.translation,
          romanization = term.romanization,
          token_count = term.tokenCount,
          tags = tagTexts,
          parents = parentTexts,
          children_count = childrenCount,
          created_at = term.created?.format(dateFormatter),
          status_changed_at = term.statusChanged?.format(dateFormatter),
      )
    }
  }

  private fun termToDto(term: Term): TermDto {
    return termsToDtos(listOf(term)).first()
  }
}
