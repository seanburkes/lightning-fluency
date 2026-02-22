package com.lute.application

import com.lute.dtos.TermDto

interface TermRelationshipService {
  fun addParent(termId: Long, parentId: Long)

  fun removeParent(termId: Long, parentId: Long)

  fun getParents(termId: Long): List<TermDto>
}
