package com.lute.application

import com.lute.application.exceptions.TermNotFoundException
import com.lute.db.repositories.TagRepository
import com.lute.db.repositories.TermRepository
import com.lute.domain.Term
import com.lute.dtos.TermDto
import java.time.format.DateTimeFormatter

class TermRelationshipServiceImpl(
    private val termRepository: TermRepository,
    private val tagRepository: TagRepository,
) : TermRelationshipService {
  private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  override fun addParent(termId: Long, parentId: Long) {
    termRepository.findById(termId) ?: throw TermNotFoundException("Term with id $termId not found")
    termRepository.findById(parentId)
        ?: throw TermNotFoundException("Parent term with id $parentId not found")

    termRepository.addParent(termId, parentId)
  }

  override fun removeParent(termId: Long, parentId: Long) {
    termRepository.removeParent(termId, parentId)
  }

  override fun getParents(termId: Long): List<TermDto> {
    termRepository.findById(termId) ?: throw TermNotFoundException("Term with id $termId not found")

    val parentIds = termRepository.getParentIdsForTerms(listOf(termId))[termId] ?: emptyList()
    val parents = termRepository.findByIds(parentIds)
    return termsToDtos(parents)
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
}
