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
│   └── HealthServiceImpl.kt  # Implementation
├── presentation/     # Controllers, routes
│   └── HealthRoute.kt
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
- **HealthRoute.kt**: Route registration with injected service

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

## Notes

- Health endpoint at `/api/health` as minimal example
- Error handling via StatusPages plugin returns generic error (security)
- Database path configurable via `LUTE_DB_PATH` env var
