package com.lute.application

import com.lute.domain.HealthResponse

interface HealthService {
  fun check(): HealthResponse
}
