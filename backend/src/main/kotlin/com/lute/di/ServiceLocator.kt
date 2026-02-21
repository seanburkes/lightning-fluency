package com.lute.di

import com.lute.application.DictionaryService
import com.lute.application.DictionaryServiceImpl
import com.lute.application.HealthService
import com.lute.application.HealthServiceImpl
import com.lute.application.LanguageService
import com.lute.application.LanguageServiceImpl
import com.lute.application.ParserService
import com.lute.application.ParserServiceImpl
import com.lute.db.DatabaseFactory
import com.lute.db.migrations.MigrationManager
import com.lute.db.repositories.BookRepository
import com.lute.db.repositories.BookRepositoryImpl
import com.lute.db.repositories.BookStatsRepository
import com.lute.db.repositories.BookStatsRepositoryImpl
import com.lute.db.repositories.DictionaryRepository
import com.lute.db.repositories.DictionaryRepositoryImpl
import com.lute.db.repositories.LanguageRepository
import com.lute.db.repositories.LanguageRepositoryImpl
import com.lute.db.repositories.SettingsRepository
import com.lute.db.repositories.SettingsRepositoryImpl
import com.lute.db.repositories.StatusRepository
import com.lute.db.repositories.StatusRepositoryImpl
import com.lute.db.repositories.TagRepository
import com.lute.db.repositories.TagRepositoryImpl
import com.lute.db.repositories.TermRepository
import com.lute.db.repositories.TermRepositoryImpl
import com.lute.db.repositories.TextRepository
import com.lute.db.repositories.TextRepositoryImpl
import com.lute.parse.ParserFactory
import com.lute.presentation.HealthRoute
import com.lute.presentation.LanguageRoutes

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
  val languageRepository: LanguageRepository by lazy { LanguageRepositoryImpl() }
  val bookRepository: BookRepository by lazy { BookRepositoryImpl() }
  val textRepository: TextRepository by lazy { TextRepositoryImpl() }
  val termRepository: TermRepository by lazy { TermRepositoryImpl() }
  val tagRepository: TagRepository by lazy { TagRepositoryImpl() }
  val statusRepository: StatusRepository by lazy { StatusRepositoryImpl() }
  val settingsRepository: SettingsRepository by lazy { SettingsRepositoryImpl() }
  val bookStatsRepository: BookStatsRepository by lazy { BookStatsRepositoryImpl() }
  val dictionaryRepository: DictionaryRepository by lazy { DictionaryRepositoryImpl() }

  val languageService: LanguageService by lazy {
    LanguageServiceImpl(languageRepository, parserFactory)
  }

  val dictionaryService: DictionaryService by lazy {
    DictionaryServiceImpl(dictionaryRepository, languageRepository)
  }

  // Parser
  val parserFactory: ParserFactory by lazy { ParserFactory() }
  val parserService: ParserService by lazy { ParserServiceImpl(parserFactory) }

  val languageRoutes: LanguageRoutes by lazy { LanguageRoutes(languageService, dictionaryService) }
}
