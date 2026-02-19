package com.lute.application

import com.lute.domain.HealthResponse

class HealthServiceImpl : HealthService {
  override fun check(): HealthResponse = HealthResponse(status = "ok")
}
