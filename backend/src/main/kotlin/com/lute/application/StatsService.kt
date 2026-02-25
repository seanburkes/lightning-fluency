package com.lute.application

import com.lute.dtos.StatsDto

interface StatsService {
  fun getStats(): StatsDto
}
