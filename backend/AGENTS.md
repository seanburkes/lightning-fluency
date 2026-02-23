# Backend Knowledge Base

**Technology**: Kotlin + Ktor + Exposed ORM  
**Architecture**: Clean Architecture (Domain → Application → Presentation → DI)

## Structure

```
backend/src/main/kotlin/com/lute/
├── domain/           # Entities, value objects, business rules
│   └── HealthResponse.kt
├── application/      # Use cases, application services
│   ├── HealthService.kt      # Interface
│   ├── HealthServiceImpl.kt  # Implementation
│   ├── exceptions/           # Exception hierarchy
│   │   ├── ApplicationExceptions.kt  # EntityNotFoundException, DuplicateEntityException, EntityInUseException
│   │   └── LanguageExceptions.kt     # ValidationException
│   └── ValidationUtils.kt    # Fluent validation builder
├── presentation/     # Controllers, routes
│   ├── HealthRoute.kt
│   ├── RouteExtensions.kt    # parseIdOrBadRequest, requireEntity helpers
│   └── ...
├── utils/           # Shared utilities
│   └── DateFormatters.kt     # ISO_LOCAL formatter with toIsoString() extension
├── di/              # Dependency injection
│   └── ServiceLocator.kt     # Manual DI container
└── Application.kt   # Ktor server setup
```

## Clean Architecture Layers

| Layer | Responsibility | Dependencies |
|-------|---------------|--------------|
| Domain | Entities, value objects, domain logic | None (innermost) |
| Application | Use cases, service interfaces | Domain only |
| Presentation | HTTP routes, request/response handling | Application, Domain |
| DI | Wiring dependencies | All layers |

## Key Files

- **Application.kt**: Ktor server configuration, plugins (ContentNegotiation, StatusPages), routing
- **ServiceLocator.kt**: Manual DI container using lazy initialization
- **RouteExtensions.kt**: Extension functions for route parameter parsing and error responses
- **ValidationUtils.kt**: Fluent validation builder with required(), maxLength(), regex()
- **ApplicationExceptions.kt**: Sealed exception hierarchy (EntityNotFoundException, DuplicateEntityException, EntityInUseException)

## Conventions

- **ktfmt**: Authoritative formatter (Google style, 2-space indent)
- **ktlint**: Lint-only mode, conflicts with ktfmt disabled via `.editorconfig`
- **Package**: `com.lute` (inherited from original Lute project)
- **DI**: ServiceLocator pattern (not Koin/Kodein runtime)

## Build Commands

```bash
./gradlew build          # Build, test, format check, lint
./gradlew test           # Run tests only
./gradlew run            # Start dev server on :8080
./gradlew ktfmtFormat    # Auto-format all Kotlin files
./gradlew ktfmtCheck     # Check formatting (CI)
./gradlew ktlintCheck    # Check lint rules
```

## Dependencies

- **Ktor 3.1.1**: Server framework (Netty, content negotiation, status pages)
- **Exposed 0.58.0**: ORM (core, JDBC, DAO, Java time)
- **SQLite 3.47.1.0**: Database driver
- **Koin 4.0.2**: Included but NOT used (using manual DI instead)
- **Logback 1.5.15**: Logging

## Testing

- Test host: `io.ktor:ktor-server-test-host`
- Content negotiation: `io.ktor:ktor-client-content-negotiation`
- Kotlin test with JUnit 5 platform

## New Utilities

### RouteExtensions
```kotlin
// Parameter parsing with error responses
val id = call.parseIdOrBadRequest("id", "book") ?: return@get
val entity = call.requireEntity(id, "Book", bookService::getBookById) ?: return@get
call.respondNotFound("Book")

// Silent parsing (no error response)
val languageId = call.parseId("language_id")
val limit = call.parseIntParam("limit") ?: 100
val archived = call.parseBooleanParam("archived")
```

### ValidationUtils
```kotlin
ValidationUtils.validator()
    .required("name", dto.name, "Language name")
    .maxLength("name", dto.name!!, 40, "Language name")
    .regex("pattern", dto.pattern, "Pattern")
    .custom("field", condition, "Error message")
    .throwIfErrors()
```

### DateFormatters
```kotlin
import com.lute.utils.DateFormatters.toIsoString

created_at = term.created.toIsoString()
```

## Notes

- Health endpoint at `/api/health` as minimal example
- Error handling via StatusPages plugin returns generic error (security)
- Database path configurable via `LUTE_DB_PATH` env var
