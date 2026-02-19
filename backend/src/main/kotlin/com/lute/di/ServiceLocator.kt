package com.lute.di

import com.lute.application.HealthService
import com.lute.application.HealthServiceImpl
import com.lute.db.DatabaseFactory
import com.lute.db.migrations.MigrationManager
import com.lute.db.repositories.BookRepository
import com.lute.db.repositories.BookStatsRepository
import com.lute.db.repositories.LanguageRepository
import com.lute.db.repositories.SettingsRepository
import com.lute.db.repositories.StatusRepository
import com.lute.db.repositories.TagRepository
import com.lute.db.repositories.TermRepository
import com.lute.db.repositories.TextRepository
import com.lute.presentation.HealthRoute

object ServiceLocator {
  // Health
  val healthService: HealthService by lazy { HealthServiceImpl() }
  val healthRoute: HealthRoute by lazy { HealthRoute(healthService) }

  // Database
  val databaseFactory: DatabaseFactory = DatabaseFactory

  val migrationManager: MigrationManager by lazy {
    val db =
        databaseFactory.database
            ?: throw IllegalStateException("Database must be initialized before MigrationManager")
    MigrationManager(db)
  }

  // Repositories
  val languageRepository: LanguageRepository by lazy { LanguageRepository() }
  val bookRepository: BookRepository by lazy { BookRepository() }
  val textRepository: TextRepository by lazy { TextRepository() }
  val termRepository: TermRepository by lazy { TermRepository() }
  val tagRepository: TagRepository by lazy { TagRepository() }
  val statusRepository: StatusRepository by lazy { StatusRepository() }
  val settingsRepository: SettingsRepository by lazy { SettingsRepository() }
  val bookStatsRepository: BookStatsRepository by lazy { BookStatsRepository() }
}
