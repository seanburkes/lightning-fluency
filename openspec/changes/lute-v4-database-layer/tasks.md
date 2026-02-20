## 1. Database Connection Setup

- [x] 1.1 Create `backend/src/main/kotlin/lute/db/DatabaseFactory.kt` with HikariCP configuration
- [x] 1.2 Configure SQLite JDBC URL with database path from config
- [x] 1.3 Configure HikariCP pool size (max=10, minIdle=2, timeout=30s)
- [x] 1.4 Set up PRAGMA foreign_keys=ON on new connections
- [x] 1.5 Create DatabaseFactory.init() function to initialize database
- [x] 1.6 Add Exposed dialect for SQLite to DatabaseFactory

## 2. Exposed ORM Tables

### 2.1 Core Tables

- [x] 2.1.1 Create `backend/src/main/kotlin/lute/db/tables/LanguagesTable.kt` with all columns
- [x] 2.1.2 Create `backend/src/main/kotlin/lute/db/tables/BooksTable.kt` with all columns
- [x] 2.1.3 Create `backend/src/main/kotlin/lute/db/tables/TextsTable.kt` with all columns
- [x] 2.1.4 Create `backend/src/main/kotlin/lute/db/tables/WordsTable.kt` with all columns
- [x] 2.1.5 Create `backend/src/main/kotlin/lute/db/tables/StatusesTable.kt` with all columns

### 2.2 Tag Tables

- [x] 2.2.1 Create `backend/src/main/kotlin/lute/db/tables/TagsTable.kt`
- [x] 2.2.2 Create `backend/src/main/kotlin/lute/db/tables/Tags2Table.kt`
- [x] 2.2.3 Create `backend/src/main/kotlin/lute/db/tables/WordTagsTable.kt`
- [x] 2.2.4 Create `backend/src/main/kotlin/lute/db/tables/BookTagsTable.kt`

### 2.3 Relationship Tables

- [x] 2.3.1 Create `backend/src/main/kotlin/lute/db/tables/WordParentsTable.kt`
- [x] 2.3.2 Create `backend/src/main/kotlin/lute/db/tables/SentencesTable.kt`

### 2.4 Supporting Tables

- [x] 2.4.1 Create `backend/src/main/kotlin/lute/db/tables/SettingsTable.kt`
- [x] 2.4.2 Create `backend/src/main/kotlin/lute/db/tables/BookStatsTable.kt`
- [x] 2.4.3 Create `backend/src/main/kotlin/lute/db/tables/WordImagesTable.kt`
- [x] 2.4.4 Create `backend/src/main/kotlin/lute/db/tables/WordFlashMessagesTable.kt`
- [x] 2.4.5 Create `backend/src/main/kotlin/lute/db/tables/WordsReadTable.kt`
- [x] 2.4.6 Create `backend/src/main/kotlin/lute/db/tables/SrsExportSpecsTable.kt`
- [x] 2.4.7 Create `backend/src/main/kotlin/lute/db/tables/TextBookmarksTable.kt`
- [x] 2.4.8 Create `backend/src/main/kotlin/lute/db/tables/LanguageDictsTable.kt`
- [x] 2.4.9 Create `backend/src/main/kotlin/lute/db/tables/MigrationsTable.kt` for tracking

### 2.5 Indexes and Keys

- [x] 2.5.1 Add all indexes to tables per baseline.sql
- [x] 2.5.2 Add foreign key constraints to all tables
- [x] 2.5.3 Create `backend/src/main/kotlin/lute/db/tables/Tables.kt` to register all tables

## 3. Repositories

### 3.1 Language Repository

- [x] 3.1.1 Create `backend/src/main/kotlin/lute/db/repositories/LanguageRepository.kt`
- [x] 3.1.2 Implement findById(id: Int): Language?
- [x] 3.1.3 Implement findByName(name: String): Language?
- [x] 3.1.4 Implement findAll(): List<Language>
- [x] 3.1.5 Implement save(language: Language): Int
- [x] 3.1.6 Implement update(language: Language)
- [x] 3.1.7 Implement delete(id: Int)

### 3.2 Term Repository

- [x] 3.2.1 Create `backend/src/main/kotlin/lute/db/repositories/TermRepository.kt`
- [x] 3.2.2 Implement findById(id: Int): Term?
- [x] 3.2.3 Implement findByTextAndLanguage(textLC: String, languageId: Int): Term?
- [x] 3.2.4 Implement findAll(languageId: Int?, status: Int?, limit: Int, offset: Int): List<Term>
- [x] 3.2.5 Implement save(term: Term): Int (auto-populate WoTextLC)
- [x] 3.2.6 Implement update(term: Term) (auto-update WoStatusChanged)
- [x] 3.2.7 Implement delete(id: Int)
- [x] 3.2.8 Implement countByLanguage(languageId: Int): Int

### 3.3 Book Repository

- [x] 3.3.1 Create `backend/src/main/kotlin/lute/db/repositories/BookRepository.kt`
- [x] 3.3.2 Implement findById(id: Int): Book?
- [x] 3.3.3 Implement findAll(languageId: Int?, archived: Boolean?): List<Book>
- [x] 3.3.4 Implement save(book: Book): Int
- [x] 3.3.5 Implement update(book: Book)
- [x] 3.3.6 Implement updateCurrentPage(bookId: Int, txId: Int)
- [x] 3.3.7 Implement delete(id: Int)

### 3.4 Text Repository

- [x] 3.4.1 Create `backend/src/main/kotlin/lute/db/repositories/TextRepository.kt`
- [x] 3.4.2 Implement findById(id: Int): Text?
- [x] 3.4.3 Implement findByBookId(bookId: Int): List<Text>
- [x] 3.4.4 Implement findByBookAndOrder(bookId: Int, order: Int): Text?
- [x] 3.4.5 Implement getCountForBook(bookId: Int): Int
- [x] 3.4.6 Implement save(text: Text): Int
- [x] 3.4.7 Implement update(text: Text)
- [x] 3.4.8 Implement delete(id: Int)

### 3.5 Tag Repository

- [x] 3.5.1 Create `backend/src/main/kotlin/lute/db/repositories/TagRepository.kt`
- [x] 3.5.2 Implement findAll(): List<Tag>
- [x] 3.5.3 Implement findByText(text: String): Tag?
- [x] 3.5.4 Implement save(tag: Tag): Int
- [x] 3.5.5 Implement addTagToTerm(termId: Int, tagId: Int)
- [x] 3.5.6 Implement removeTagFromTerm(termId: Int, tagId: Int)
- [x] 3.5.7 Implement getTagsForTerm(termId: Int): List<Tag>

### 3.6 Status Repository

- [x] 3.6.1 Create `backend/src/main/kotlin/lute/db/repositories/StatusRepository.kt`
- [x] 3.6.2 Implement findAll(): List<Status>
- [x] 3.6.3 Implement findById(id: Int): Status?

### 3.7 Settings Repository

- [x] 3.7.1 Create `backend/src/main/kotlin/lute/db/repositories/SettingsRepository.kt`
- [x] 3.7.2 Implement get(key: String): String?
- [x] 3.7.3 Implement set(key: String, value: String, type: String)
- [x] 3.7.4 Implement getAll(): Map<String, Setting>

### 3.8 Book Stats Repository

- [x] 3.8.1 Create `backend/src/main/kotlin/lute/db/repositories/BookStatsRepository.kt`
- [x] 3.8.2 Implement findByBookId(bookId: Int): BookStats?
- [x] 3.8.3 Implement update(bookStats: BookStats)
- [x] 3.8.4 Implement calculateAndSave(bookId: Int)

## 4. Migration System

- [x] 4.1 Create `backend/src/main/kotlin/lute/db/migrations/MigrationManager.kt`
- [x] 4.2 Implement loadMigrationsFromResources(path: String): List<String>
- [x] 4.3 Implement getAppliedMigrations(): Set<String>
- [x] 4.4 Implement applyMigration(sql: String)
- [x] 4.5 Implement runMigrations() that applies pending migrations
- [x] 4.6 Create initial schema SQL file at `backend/src/main/resources/db/migrations/V1__initial_schema.sql`
- [x] 4.7 Create triggers SQL file at `backend/src/main/resources/db/repeatable/triggers.sql`
- [x] 4.8 Add seed data insertion (statuses, default settings)
- [x] 4.9 Handle migration failures with proper error messages

## 5. Domain Models

- [x] 5.1 Create `backend/src/main/kotlin/lute/models/Language.kt` data class
- [x] 5.2 Create `backend/src/main/kotlin/lute/models/Book.kt` data class
- [x] 5.3 Create `backend/src/main/kotlin/lute/models/Text.kt` data class
- [x] 5.4 Create `backend/src/main/kotlin/lute/models/Term.kt` data class
- [x] 5.5 Create `backend/src/main/kotlin/lute/models/Tag.kt` data class
- [x] 5.6 Create `backend/src/main/kotlin/lute/models/Status.kt` data class
- [x] 5.7 Create `backend/src/main/kotlin/lute/models/Setting.kt` data class
- [x] 5.8 Create `backend/src/main/kotlin/lute/models/BookStats.kt` data class
- [x] 5.9 Add mapping functions between Exposed entities and domain models

## 6. Dependency Injection

- [x] 6.1 Register DatabaseFactory in ServiceLocator (adapted from Koin to manual DI)
- [x] 6.2 Register MigrationManager in ServiceLocator
- [x] 6.3 Register all repositories in ServiceLocator
- [x] 6.4 Update Application.kt to initialize database on startup

## 7. Tests

### 7.1 Repository Tests

- [x] 7.1.1 Create `backend/src/test/kotlin/lute/db/repositories/LanguageRepositoryTest.kt`
- [x] 7.1.2 Create `backend/src/test/kotlin/lute/db/repositories/TermRepositoryTest.kt`
- [x] 7.1.3 Create `backend/src/test/kotlin/lute/db/repositories/BookRepositoryTest.kt`
- [x] 7.1.4 Create `backend/src/test/kotlin/lute/db/repositories/TextRepositoryTest.kt`
- [x] 7.1.5 Create `backend/src/test/kotlin/lute/db/repositories/TagRepositoryTest.kt`
- [x] 7.1.6 Create `backend/src/test/kotlin/lute/db/repositories/SettingsRepositoryTest.kt`

### 7.2 Migration Tests

- [x] 7.2.1 Create test that runs migrations on in-memory database
- [x] 7.2.2 Verify all tables are created correctly
- [x] 7.2.3 Verify all indexes exist
- [x] 7.2.4 Verify seed data is inserted
- [x] 7.2.5 Verify skipping already-applied migrations works

## 8. Verification

- [x] 8.1 Run `./gradlew test` - all tests pass
- [x] 8.2 Run `./gradlew ktlintCheck` - no errors
- [x] 8.3 Verify database can be initialized from scratch
- [x] 8.4 Verify foreign key constraints work (test delete with children)
- [x] 8.5 Verify triggers fire correctly (test status sync)
- [x] 8.6 Run integration test that creates language, book, text, and terms
