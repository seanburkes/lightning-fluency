# Lute v4 - Rewrite Specification

**Target Stack**: Kotlin (Ktor) + SvelteKit + Skeleton UI + Docker + PWA

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Core Features](#core-features)
3. [OpenSpec Proposals](#openspec-proposals)
4. [Technical Approach](#technical-approach)
5. [Testing Strategy](#testing-strategy)
6. [Migration Path](#migration-path)

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         SvelteKit PWA Frontend                          │
├─────────────────────────────────────────────────────────────────────────┤
│  Stack: SvelteKit + Skeleton UI + @vite-pwa/sveltekit                   │
│  Features:                                                               │
│  • Server-side rendering + client-side hydration                        │
│  • PWA with offline reading support                                      │
│  • Skeleton UI components (Dialog, DataTable, Forms)                    │
│  • Global keyboard shortcut handling                                     │
│  • Svelte stores for state management                                    │
└──────────────────────────────────┬──────────────────────────────────────┘
                                   │ REST API (JSON)
                                   ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         Kotlin Backend (Ktor)                           │
├─────────────────────────────────────────────────────────────────────────┤
│  Stack: Ktor + Exposed ORM + SQLite                                     │
│  Architecture:                                                           │
│  • Routes (HTTP layer)                                                   │
│  • Services (business logic)                                             │
│  • Repositories (data access)                                            │
│  • Parsers (language-specific text processing)                          │
│  Deployment: Docker or GraalVM native binary                            │
└──────────────────────────────────┬──────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                            SQLite Database                              │
│  Same schema as Lute v3 (migrate existing data)                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Core Features

### 1. Reading Interface

**Priority: CRITICAL** | **Complexity: HIGH**

| Feature | Description | Implementation Notes |
|---------|-------------|---------------------|
| **Page Navigation** | Navigate between book pages, track current position | Server tracks `current_tx_id`, client fetches pages via API |
| **Text Rendering** | Parse text into paragraphs, sentences, terms with status highlighting | Backend renders `TextItem[]` with term associations; client applies CSS classes |
| **Term Hover Popups** | Show translation, romanization, tags, parents, components on hover | Tooltip component fetches `/api/terms/:id/popup` on demand |
| **Click Selection** | Single click selects term for editing; double-click opens edit form | Track `selectedTermId` in Svelte store |
| **Multiword Selection** | Long-press/drag to select word ranges, create phrase terms | Touch events + mouse drag detection |
| **Status Updates** | Change term status via click, keyboard, or bulk operations | PATCH `/api/terms/:id` with `status` field |
| **Keyboard Shortcuts** | Navigation (prev/next unknown), status changes, page operations | Global keydown handler in root layout |
| **Focus Mode** | Hide menus for distraction-free reading | Toggle state in settings store |
| **RTL Support** | Right-to-left text rendering | `dir="rtl"` on reading pane container |

**Key Files (Current)**:
- `lute/read/routes.py` - 15+ endpoints
- `lute/read/service.py` - Popup data, bulk updates
- `lute/read/render/service.py` - Text rendering, term matching
- `lute/static/js/lute.js` - 500+ lines of interactions

**API Endpoints (New)**:
```
GET    /api/books/:id/pages/:num      # Get page content with rendered terms
PATCH  /api/terms/:id                  # Update single term
POST   /api/terms/bulk                 # Bulk status update
GET    /api/terms/:id/popup            # Get popup data
POST   /api/reading/complete           # Mark page as read
```

---

### 2. Term Management

**Priority: CRITICAL** | **Complexity: MEDIUM**

| Feature | Description | Implementation Notes |
|---------|-------------|---------------------|
| **CRUD Operations** | Create, read, update, delete terms | RESTful endpoints |
| **Bulk Operations** | Mass status change, parent assignment, tagging | DataTable with row selection |
| **Parent/Child Relationships** | Terms can have multiple parents; status sync optional | Many-to-many with `wordparents` table |
| **Tagging System** | User-defined tags for categorization | Tagify-style autocomplete input |
| **Search & Filtering** | Filter by language, status, tags, age, text search | Server-side query building |
| **Inline Editing** | Click-to-edit in DataTable rows | ContentEditable or inline form |
| **Import/Export** | CSV import and export of terms | File upload/download |

**Key Files (Current)**:
- `lute/term/routes.py` - 20+ endpoints
- `lute/term/service.py` - Bulk updates, validation
- `lute/models/term.py` - Term entity with relationships

**API Endpoints (New)**:
```
GET    /api/terms                       # List with filters
GET    /api/terms/:id                   # Get single term
POST   /api/terms                       # Create term
PATCH  /api/terms/:id                   # Update term
DELETE /api/terms/:id                   # Delete term
POST   /api/terms/bulk                  # Bulk operations
GET    /api/terms/export                # CSV export
POST   /api/terms/import                # CSV import
GET    /api/terms/:id/sentences         # Usage examples
```

---

### 3. Book Management

**Priority: HIGH** | **Complexity: MEDIUM**

| Feature | Description | Implementation Notes |
|---------|-------------|---------------------|
| **Book Creation** | Manual text entry, file upload, webpage import | Support .txt, .epub, .pdf, .srt, .vtt |
| **Automatic Pagination** | Split books into pages by word count | Configurable threshold (default 250 words) |
| **Book Organization** | Tags, language, archive status | BookTag many-to-many |
| **Reading Progress** | Track current page, start/read dates | `current_tx_id`, `TxStartDate`, `TxReadDate` |
| **Audio Support** | Upload audio files, sync with reading | Position tracking, bookmarks |
| **Book Statistics** | Term counts, unknown percentage, status distribution | Calculated and cached in `bookstats` |

**Key Files (Current)**:
- `lute/book/routes.py` - CRUD + import
- `lute/book/service.py` - File parsing, pagination
- `lute/models/book.py` - Book, Text, BookTag entities

**API Endpoints (New)**:
```
GET    /api/books                       # List books
GET    /api/books/:id                   # Get book details
POST   /api/books                       # Create book
PATCH  /api/books/:id                   # Update book
DELETE /api/books/:id                   # Delete book
POST   /api/books/import                # Import from file/URL
GET    /api/books/:id/stats             # Get statistics
POST   /api/books/:id/audio             # Upload audio
```

---

### 4. Parser System

**Priority: HIGH** | **Complexity: HIGH**

| Parser | Language | Implementation |
|--------|----------|----------------|
| **Space Delimited** | English, Spanish, French, etc. | Regex word character matching |
| **Turkish** | Turkish | Special lowercase handling (İ→i, I→ı) |
| **Japanese** | Japanese | Kuromoji (Java) via ServiceLoader |
| **Classical Chinese** | Classical Chinese | Character-by-character |
| **Mandarin (Plugin)** | Mandarin Chinese | Jieba + pypinyin (needs port) |
| **Thai (Plugin)** | Thai | PyThaiNLP (needs port) |

**Parser Interface (Kotlin)**:
```kotlin
interface Parser {
    val name: String
    val supportedLanguageCodes: List<String>
    
    fun parse(text: String, language: Language): List<ParsedToken>
    fun getReading(text: String): String?
    fun getLowercase(text: String): String
}

data class ParsedToken(
    val token: String,
    val isWord: Boolean,
    val isEndOfSentence: Boolean,
    val order: Int,
    val sentenceNumber: Int
)
```

**Language Configuration**:
- `parser_type`: Parser selection
- `character_substitutions`: Pre-parsing replacements
- `regexp_split_sentences`: Sentence terminators
- `exceptions_split_sentences`: Don't-split patterns
- `word_characters`: Regex for word boundaries
- `right_to_left`: RTL flag
- `show_romanization`: Display pronunciation field

---

### 5. Settings & Configuration

**Priority: MEDIUM** | **Complexity: LOW**

| Category | Settings |
|----------|----------|
| **Theme** | Theme selection, custom CSS |
| **Backup** | Enable, auto, directory, count, warnings |
| **Keyboard** | Customizable hotkeys per action |
| **Anki** | AnkiConnect URL, enable/disable |
| **Reading** | Show highlights, focus mode, tap behavior |
| **Current Language** | Default language for filtering |

**API Endpoints**:
```
GET    /api/settings                    # Get all settings
PUT    /api/settings/:key               # Update setting
GET    /api/themes                      # List themes
GET    /api/themes/current              # Current theme CSS
```

---

### 6. Anki Integration

**Priority: MEDIUM** | **Complexity: MEDIUM**

| Feature | Description |
|---------|-------------|
| **Export Specs** | Named configurations with criteria, deck, field mappings |
| **Criteria Language** | `status > 1`, `tags:["noun"]`, `language:"German"` |
| **Field Mapping** | `{term}`, `{translation}`, `{image}`, `{sentence}` |
| **Media Upload** | Automatic image upload to Anki media folder |
| **Validation** | Test connection, validate deck/note types |

**API Endpoints**:
```
GET    /api/anki/specs                  # List export specs
POST   /api/anki/specs                  # Create spec
GET    /api/anki/decks                  # List Anki decks
GET    /api/anki/note-types             # List note types
POST   /api/anki/export                 # Export terms to Anki
```

---

### 7. PWA Features

**Priority: HIGH** | **Complexity: MEDIUM**

| Feature | Description |
|---------|-------------|
| **Offline Reading** | Cache book pages for offline access |
| **Install Prompt** | Add to homescreen |
| **Background Sync** | Queue term updates when offline |
| **Push Notifications** | (Future) Reading reminders |

**Implementation**:
- Use `@vite-pwa/sveltekit` plugin
- Cache-first strategy for book pages
- Network-first for term updates
- IndexedDB for offline queue

---

## OpenSpec Proposals

Iterative implementation approach with focused proposals:

### Phase 1: Foundation (Weeks 1-4)

| Proposal | Scope | Dependencies |
|----------|-------|--------------|
| **OSP-001: Project Setup** | Repo structure, build config, CI/CD | None |
| **OSP-002: Database Layer** | Exposed models, migrations, repositories | OSP-001 |
| **OSP-003: Auth & Users** | User model, session management (optional) | OSP-002 |
| **OSP-004: Language CRUD** | Language management endpoints | OSP-002 |

### Phase 2: Core Reading (Weeks 5-10)

| Proposal | Scope | Dependencies |
|----------|-------|--------------|
| **OSP-005: Parser System** | Parser interface, space-delimited, Turkish | OSP-002 |
| **OSP-006: Book Management** | Book CRUD, import, pagination | OSP-005 |
| **OSP-007: Term Management** | Term CRUD, search, tagging | OSP-002 |
| **OSP-008: Reading Interface** | Page rendering, term highlighting | OSP-005, OSP-006 |
| **OSP-009: Term Popups** | Hover tooltips, edit forms | OSP-007, OSP-008 |

### Phase 3: Enhanced Features (Weeks 11-14)

| Proposal | Scope | Dependencies |
|----------|-------|--------------|
| **OSP-010: Keyboard Shortcuts** | Global hotkey system | OSP-008 |
| **OSP-011: Audio Player** | Audio playback, bookmarks | OSP-006 |
| **OSP-012: Statistics** | Book stats, reading progress | OSP-006, OSP-008 |
| **OSP-013: Japanese Parser** | Kuromoji integration | OSP-005 |

### Phase 4: Polish & Integration (Weeks 15-18)

| Proposal | Scope | Dependencies |
|----------|-------|--------------|
| **OSP-014: Anki Export** | AnkiConnect integration | OSP-007 |
| **OSP-015: PWA Setup** | Offline support, install prompt | All |
| **OSP-016: Backup System** | Database backup/restore | OSP-002 |
| **OSP-017: Settings UI** | Settings pages, hotkey config | OSP-010 |

---

## Technical Approach

### Backend (Kotlin/Ktor)

**Project Structure**:
```
backend/
├── build.gradle.kts
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   ├── Application.kt
│   │   │   ├── config/
│   │   │   │   ├── DatabaseFactory.kt
│   │   │   │   └── DependencyInjection.kt
│   │   │   ├── routes/
│   │   │   │   ├── TermRoutes.kt
│   │   │   │   ├── BookRoutes.kt
│   │   │   │   ├── LanguageRoutes.kt
│   │   │   │   └── ReadingRoutes.kt
│   │   │   ├── services/
│   │   │   │   ├── TermService.kt
│   │   │   │   ├── BookService.kt
│   │   │   │   └── ReadingService.kt
│   │   │   ├── models/
│   │   │   │   ├── Term.kt
│   │   │   │   ├── Book.kt
│   │   │   │   └── Language.kt
│   │   │   ├── db/
│   │   │   │   ├── tables/
│   │   │   │   └── repositories/
│   │   │   └── parsers/
│   │   │       ├── Parser.kt
│   │   │       ├── SpaceDelimitedParser.kt
│   │   │       └── JapaneseParser.kt
│   │   └── resources/
│   └── test/
└── Dockerfile
```

**Key Dependencies**:
```kotlin
// build.gradle.kts
dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    
    // Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.45.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.45.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.45.0")
    
    // SQLite
    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
    
    // DI
    implementation("org.koin:koin-ktor:3.5.3")
    
    // Japanese parser
    implementation("com.atilika.kuromoji:kuromoji-ipadic:0.9.0")
}
```

### Frontend (SvelteKit + Skeleton UI)

**Project Structure**:
```
frontend/
├── package.json
├── vite.config.ts
├── svelte.config.js
├── src/
│   ├── routes/
│   │   ├── +layout.svelte          # App shell
│   │   ├── +page.svelte            # Home
│   │   ├── read/
│   │   │   └── [bookId]/
│   │   │       └── [pageNum]/
│   │   │           └── +page.svelte
│   │   ├── terms/
│   │   │   ├── +page.svelte
│   │   │   └── [id]/
│   │   │       └── +page.svelte
│   │   ├── books/
│   │   ├── languages/
│   │   └── settings/
│   ├── lib/
│   │   ├── api/
│   │   │   └── client.ts
│   │   ├── components/
│   │   │   ├── TermPopup.svelte
│   │   │   ├── ReadingPane.svelte
│   │   │   └── DataTable.svelte
│   │   └── stores/
│   │       ├── terms.ts
│   │       └── settings.ts
│   └── static/
│       ├── manifest.json
│       └── sw.js
└── Dockerfile
```

**Key Dependencies**:
```json
{
  "dependencies": {
    "@skeletonlabs/skeleton-svelte": "^4",
    "@skeletonlabs/skeleton": "^4"
  },
  "devDependencies": {
    "@sveltejs/kit": "^2",
    "@vite-pwa/sveltekit": "^1.1.0",
    "@vincjo/datatables": "^1.0.0",
    "typescript": "^5"
  }
}
```

### Docker Deployment

```dockerfile
# Multi-stage build
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle buildFatJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml**:
```yaml
services:
  lute:
    build: .
    ports:
      - "8080:8080"
    volumes:
      - ./data:/app/data
      - ./backups:/app/backups
    environment:
      - LUTE_DB_PATH=/app/data/lute.db
```

---

## Testing Strategy

### Backend Tests (Kotlin)

**Unit Tests** (Kotest + MockK):
```kotlin
class TermServiceTest : DescribeSpec({
    describe("TermService") {
        it("should create term with all fields") {
            // Test implementation
        }
        
        it("should update term status") {
            // Test implementation
        }
    }
})
```

**Integration Tests** (Ktor Test Application):
```kotlin
class TermRoutesTest : DescribeSpec({
    describe("GET /api/terms") {
        it("should return terms with filters") {
            testApplication {
                // Setup and assertions
            }
        }
    }
})
```

### Frontend Tests (Vitest + Playwright)

**Component Tests**:
```typescript
// TermPopup.test.ts
import { render, fireEvent } from '@testing-library/svelte';
import TermPopup from '$lib/components/TermPopup.svelte';

test('shows translation on render', () => {
    const { getByText } = render(TermPopup, { term: mockTerm });
    expect(getByText('Hello')).toBeInTheDocument();
});
```

**E2E Tests** (Playwright):
```typescript
// reading.spec.ts
test('can read book and update term status', async ({ page }) => {
    await page.goto('/');
    await page.click('.book-card:first-child');
    await page.click('.token.unknown');
    await expect(page.locator('.popup')).toBeVisible();
    await page.click('.status-btn:first-child');
});
```

---

## Migration Path

### Phase 1: Parallel Operation
1. Deploy Kotlin backend alongside Flask app
2. Run both on different ports
3. Test API compatibility

### Phase 2: Data Migration
1. Export SQLite database from Flask app
2. Import to new system (schema-compatible)
3. Verify data integrity

### Phase 3: Cutover
1. Switch frontend to new backend
2. Monitor for issues
3. Decommission Flask app

---

## References

**Documentation**:
- [Ktor Documentation](https://ktor.io/docs/)
- [Exposed ORM](https://www.jetbrains.com/help/exposed/home.html)
- [Skeleton UI](https://www.skeleton.dev/)
- [SvelteKit](https://kit.svelte.dev/docs)
- [@vite-pwa/sveltekit](https://vite-pwa-org.netlify.app/frameworks/sveltekit)

**Examples**:
- [Ktor + Exposed Sample](https://github.com/JetBrains/Exposed/tree/main/samples/exposed-ktor-r2dbc)
- [Skeleton DataTables Integration](https://github.com/skeletonlabs/skeleton-datatables-integration)
- [GraalVM Native Ktor](https://github.com/antonarhipov/ktor-native-image)
