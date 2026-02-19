package com.lute.di

import com.lute.application.HealthService
import com.lute.application.HealthServiceImpl
import com.lute.presentation.HealthRoute

object ServiceLocator {
  val healthService: HealthService by lazy { HealthServiceImpl() }
  val healthRoute: HealthRoute by lazy { HealthRoute(healthService) }
}
