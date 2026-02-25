package com.lute.di

import com.lute.application.AudioService
import com.lute.application.AudioServiceImpl
import com.lute.application.BookCrudService
import com.lute.application.BookCrudServiceImpl
import com.lute.application.BookPageService
import com.lute.application.BookPageServiceImpl
import com.lute.application.BookService
import com.lute.application.BookServiceImpl
import com.lute.application.BookStatsService
import com.lute.application.BookStatsServiceImpl
import com.lute.application.BookTagService
import com.lute.application.BookTagServiceImpl
import com.lute.application.DictionaryService
import com.lute.application.DictionaryServiceImpl
import com.lute.application.HealthService
import com.lute.application.HealthServiceImpl
import com.lute.application.LanguageCrudService
import com.lute.application.LanguageCrudServiceImpl
import com.lute.application.LanguageService
import com.lute.application.LanguageServiceImpl
import com.lute.application.LanguageValidationService
import com.lute.application.LanguageValidationServiceImpl
import com.lute.application.ParserService
import com.lute.application.ParserServiceImpl
import com.lute.application.PopupService
import com.lute.application.PopupServiceImpl
import com.lute.application.ReadingService
import com.lute.application.ReadingServiceImpl
import com.lute.application.SentenceParser
import com.lute.application.SentenceParserImpl
import com.lute.application.TermBulkService
import com.lute.application.TermBulkServiceImpl
import com.lute.application.TermCrudService
import com.lute.application.TermCrudServiceImpl
import com.lute.application.TermCsvService
import com.lute.application.TermCsvServiceImpl
import com.lute.application.TermRelationshipService
import com.lute.application.TermRelationshipServiceImpl
import com.lute.application.TermService
import com.lute.application.TermServiceImpl
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
import com.lute.presentation.BookRoutes
import com.lute.presentation.HealthRoute
import com.lute.presentation.LanguageRoutes
import com.lute.presentation.ReadingRoutes
import com.lute.presentation.TermRoutes

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

  val languageValidationService: LanguageValidationService by lazy {
    LanguageValidationServiceImpl(languageRepository, parserFactory)
  }

  val languageCrudService: LanguageCrudService by lazy {
    LanguageCrudServiceImpl(languageRepository, parserFactory, languageValidationService)
  }

  val languageService: LanguageService by lazy {
    LanguageServiceImpl(languageCrudService, languageValidationService)
  }

  val dictionaryService: DictionaryService by lazy {
    DictionaryServiceImpl(dictionaryRepository, languageRepository)
  }

  val bookPageService: BookPageService by lazy {
    BookPageServiceImpl(bookRepository, textRepository)
  }

  val bookTagService: BookTagService by lazy { BookTagServiceImpl(bookRepository, tagRepository) }

  val bookCrudService: BookCrudService by lazy {
    BookCrudServiceImpl(
        bookRepository,
        textRepository,
        languageRepository,
        tagRepository,
        bookPageService,
    )
  }

  val bookService: BookService by lazy {
    BookServiceImpl(
        bookCrudService,
        bookPageService,
        bookTagService,
    )
  }

  val bookStatsService: BookStatsService by lazy {
    BookStatsServiceImpl(bookRepository, bookStatsRepository)
  }

  val audioService: AudioService by lazy { AudioServiceImpl(bookRepository) }

  val bookRoutes: BookRoutes by lazy { BookRoutes(bookService, audioService) }

  val termService: TermService by lazy {
    TermServiceImpl(termCrudService, termBulkService, termCsvService, termRelationshipService)
  }

  val termRoutes: TermRoutes by lazy { TermRoutes(termService) }

  val termCrudService: TermCrudService by lazy {
    TermCrudServiceImpl(termRepository, languageRepository, tagRepository)
  }

  val termBulkService: TermBulkService by lazy {
    TermBulkServiceImpl(termRepository, tagRepository)
  }

  val termCsvService: TermCsvService by lazy {
    TermCsvServiceImpl(termRepository, languageRepository, tagRepository)
  }

  val termRelationshipService: TermRelationshipService by lazy {
    TermRelationshipServiceImpl(termRepository, tagRepository)
  }

  // Parser
  val parserFactory: ParserFactory by lazy { ParserFactory() }
  val parserService: ParserService by lazy { ParserServiceImpl(parserFactory) }

  // Reading
  val readingService: ReadingService by lazy {
    ReadingServiceImpl(
        bookRepository,
        textRepository,
        termRepository,
        languageRepository,
        parserService,
    )
  }
  val readingRoutes: ReadingRoutes by lazy { ReadingRoutes(readingService, popupService) }

  val sentenceParser: SentenceParser by lazy { SentenceParserImpl(parserService) }

  // Popup
  val popupService: PopupService by lazy {
    PopupServiceImpl(
        bookRepository,
        textRepository,
        termRepository,
        languageRepository,
        parserService,
        termCrudService,
        sentenceParser,
    )
  }

  val languageRoutes: LanguageRoutes by lazy { LanguageRoutes(languageService, dictionaryService) }
}
