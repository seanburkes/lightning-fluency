package com.lute.application

import com.lute.db.repositories.StatusRepository
import com.lute.db.repositories.TermRepository
import com.lute.dtos.StatsDto

class StatsServiceImpl(
    private val termRepository: TermRepository,
    private val statusRepository: StatusRepository,
) : StatsService {
  override fun getStats(): StatsDto {
    val statuses = statusRepository.findAll()
    val statusDistribution =
        statuses.associate { status ->
          val count = termRepository.countByStatus(status.id.toInt())
          status.text to count
        }
    val totalTerms = statusDistribution.values.sum()

    return StatsDto(
        total_terms = totalTerms,
        status_distribution = statusDistribution,
    )
  }
}
