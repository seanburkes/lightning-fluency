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

- [ ] 2.5.1 Add all indexes to tables per baseline.sql
- [ ] 2.5.2 Add foreign key constraints to all tables
- [x] 2.5.3 Create `backend/src/main/kotlin/lute/db/tables/Tables.kt` to register all tables

## 3. Repositories

### 3.1 Language Repository

- [ ] 3.1.1 Create `backend/src/main/kotlin/lute/db/repositories/LanguageRepository.kt`
- [ ] 3.1.2 Implement findById(id: Int): Language?
- [ ] 3.1.3 Implement findByName(name: String): Language?
- [ ] 3.1.4 Implement findAll(): List<Language>
- [ ] 3.1.5 Implement save(language: Language): Int
- [ ] 3.1.6 Implement update(language: Language)
- [ ] 3.1.7 Implement delete(id: Int)

### 3.2 Term Repository

- [ ] 3.2.1 Create `backend/src/main/kotlin/lute/db/repositories/TermRepository.kt`
- [ ] 3.2.2 Implement findById(id: Int): Term?
- [ ] 3.2.3 Implement findByTextAndLanguage(textLC: String, languageId: Int): Term?
- [ ] 3.2.4 Implement findAll(languageId: Int?, status: Int?, limit: Int, offset: Int): List<Term>
- [ ] 3.2.5 Implement save(term: Term): Int (auto-populate WoTextLC)
- [ ] 3.2.6 Implement update(term: Term) (auto-update WoStatusChanged)
- [ ] 3.2.7 Implement delete(id: Int)
- [ ] 3.2.8 Implement countByLanguage(languageId: Int): Int

### 3.3 Book Repository

- [ ] 3.3.1 Create `backend/src/main/kotlin/lute/db/repositories/BookRepository.kt`
- [ ] 3.3.2 Implement findById(id: Int): Book?
- [ ] 3.3.3 Implement findAll(languageId: Int?, archived: Boolean?): List<Book>
- [ ] 3.3.4 Implement save(book: Book): Int
- [ ] 3.3.5 Implement update(book: Book)
- [ ] 3.3.6 Implement updateCurrentPage(bookId: Int, txId: Int)
- [ ] 3.3.7 Implement delete(id: Int)

### 3.4 Text Repository

- [ ] 3.4.1 Create `backend/src/main/kotlin/lute/db/repositories/TextRepository.kt`
- [ ] 3.4.2 Implement findById(id: Int): Text?
- [ ] 3.4.3 Implement findByBookId(bookId: Int): List<Text>
- [ ] 3.4.4 Implement findByBookAndOrder(bookId: Int, order: Int): Text?
- [ ] 3.4.5 Implement getCountForBook(bookId: Int): Int
- [ ] 3.4.6 Implement save(text: Text): Int
- [ ] 3.4.7 Implement update(text: Text)
- [ ] 3.4.8 Implement delete(id: Int)

### 3.5 Tag Repository

- [ ] 3.5.1 Create `backend/src/main/kotlin/lute/db/repositories/TagRepository.kt`
- [ ] 3.5.2 Implement findAll(): List<Tag>
- [ ] 3.5.3 Implement findByText(text: String): Tag?
- [ ] 3.5.4 Implement save(tag: Tag): Int
- [ ] 3.5.5 Implement addTagToTerm(termId: Int, tagId: Int)
- [ ] 3.5.6 Implement removeTagFromTerm(termId: Int, tagId: Int)
- [ ] 3.5.7 Implement getTagsForTerm(termId: Int): List<Tag>

### 3.6 Status Repository

- [ ] 3.6.1 Create `backend/src/main/kotlin/lute/db/repositories/StatusRepository.kt`
- [ ] 3.6.2 Implement findAll(): List<Status>
- [ ] 3.6.3 Implement findById(id: Int): Status?

### 3.7 Settings Repository

- [ ] 3.7.1 Create `backend/src/main/kotlin/lute/db/repositories/SettingsRepository.kt`
- [ ] 3.7.2 Implement get(key: String): String?
- [ ] 3.7.3 Implement set(key: String, value: String, type: String)
- [ ] 3.7.4 Implement getAll(): Map<String, Setting>

### 3.8 Book Stats Repository

- [ ] 3.8.1 Create `backend/src/main/kotlin/lute/db/repositories/BookStatsRepository.kt`
- [ ] 3.8.2 Implement findByBookId(bookId: Int): BookStats?
- [ ] 3.8.3 Implement update(bookStats: BookStats)
- [ ] 3.8.4 Implement calculateAndSave(bookId: Int)

## 4. Migration System

- [ ] 4.1 Create `backend/src/main/kotlin/lute/db/migrations/MigrationManager.kt`
- [ ] 4.2 Implement loadMigrationsFromResources(path: String): List<String>
- [ ] 4.3 Implement getAppliedMigrations(): Set<String>
- [ ] 4.4 Implement applyMigration(sql: String)
- [ ] 4.5 Implement runMigrations() that applies pending migrations
- [ ] 4.6 Create initial schema SQL file at `backend/src/main/resources/db/migrations/V1__initial_schema.sql`
- [ ] 4.7 Create triggers SQL file at `backend/src/main/resources/db/repeatable/triggers.sql`
- [ ] 4.8 Add seed data insertion (statuses, default settings)
- [ ] 4.9 Handle migration failures with proper error messages

## 5. Domain Models

- [ ] 5.1 Create `backend/src/main/kotlin/lute/models/Language.kt` data class
- [ ] 5.2 Create `backend/src/main/kotlin/lute/models/Book.kt` data class
- [ ] 5.3 Create `backend/src/main/kotlin/lute/models/Text.kt` data class
- [ ] 5.4 Create `backend/src/main/kotlin/lute/models/Term.kt` data class
- [ ] 5.5 Create `backend/src/main/kotlin/lute/models/Tag.kt` data class
- [ ] 5.6 Create `backend/src/main/kotlin/lute/models/Status.kt` data class
- [ ] 5.7 Create `backend/src/main/kotlin/lute/models/Setting.kt` data class
- [ ] 5.8 Create `backend/src/main/kotlin/lute/models/BookStats.kt` data class
- [ ] 5.9 Add mapping functions between Exposed entities and domain models

## 6. Dependency Injection

- [ ] 6.1 Create `backend/src/main/kotlin/lute/db/DatabaseModule.kt` Koin module
- [ ] 6.2 Register DatabaseFactory
- [ ] 6.3 Register all repositories in Koin
- [ ] 6.4 Update Application.kt to initialize database on startup

## 7. Tests

### 7.1 Repository Tests

- [ ] 7.1.1 Create `backend/src/test/kotlin/lute/db/repositories/LanguageRepositoryTest.kt`
- [ ] 7.1.2 Create `backend/src/test/kotlin/lute/db/repositories/TermRepositoryTest.kt`
- [ ] 7.1.3 Create `backend/src/test/kotlin/lute/db/repositories/BookRepositoryTest.kt`
- [ ] 7.1.4 Create `backend/src/test/kotlin/lute/db/repositories/TextRepositoryTest.kt`
- [ ] 7.1.5 Create `backend/src/test/kotlin/lute/db/repositories/TagRepositoryTest.kt`
- [ ] 7.1.6 Create `backend/src/test/kotlin/lute/db/repositories/SettingsRepositoryTest.kt`

### 7.2 Migration Tests

- [ ] 7.2.1 Create test that runs migrations on in-memory database
- [ ] 7.2.2 Verify all tables are created correctly
- [ ] 7.2.3 Verify all indexes exist
- [ ] 7.2.4 Verify seed data is inserted
- [ ] 7.2.5 Verify skipping already-applied migrations works

## 8. Verification

- [ ] 8.1 Run `./gradlew test` - all tests pass
- [ ] 8.2 Run `./gradlew ktlintCheck` - no errors
- [ ] 8.3 Verify database can be initialized from scratch
- [ ] 8.4 Verify foreign key constraints work (test delete with children)
- [ ] 8.5 Verify triggers fire correctly (test status sync)
- [ ] 8.6 Run integration test that creates language, book, text, and terms
