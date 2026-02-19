# Lightning Fluency Architecture

## Overview

Lightning Fluency is a language learning application built as a monorepo with a Kotlin/Ktor backend and SvelteKit frontend. Originally forked from Lute v3, this project focuses on exploring and implementing evidence-based educational theories of language acquisition.

## Research Focus

This application serves as a platform for experimenting with pedagogical approaches grounded in contemporary language acquisition research, including:
- Comprehensible input theory
- Spaced repetition optimization
- Contextual vocabulary acquisition
- Multi-modal learning (visual, auditory, textual)

## Project Structure

```
lightning-fluency/
├── backend/               # Kotlin/Ktor REST API
│   ├── build.gradle.kts   # Gradle build config (Kotlin DSL)
│   ├── src/main/kotlin/   # Application source
│   ├── src/test/kotlin/   # Tests
│   └── Dockerfile         # Multi-stage Docker build
├── frontend/              # SvelteKit + Skeleton UI
│   ├── package.json       # npm dependencies and scripts
│   ├── src/routes/        # SvelteKit pages
│   ├── src/lib/           # Shared components and utilities
│   └── Dockerfile         # Multi-stage Docker build
├── docs/                  # Documentation
├── .github/workflows/     # CI/CD pipeline
├── docker-compose.yml     # Local development orchestration
├── .pre-commit-config.yaml # Git hooks
└── lute/                  # Existing v3 code (untouched)
```

## Technology Stack

### Backend
- **Language**: Kotlin 2.1.10
- **Framework**: Ktor 3.1.1
- **ORM**: Exposed 0.58.0
- **Database**: SQLite (via sqlite-jdbc 3.47.1.0)
- **DI**: Koin 4.0.2
- **Build**: Gradle 8.12 with Kotlin DSL
- **Target**: Java 21

### Frontend
- **Framework**: SvelteKit 2 (Svelte 5)
- **UI Library**: Skeleton UI v4
- **Styling**: Tailwind CSS v4
- **Build**: Vite 7

### Infrastructure
- **Containerization**: Docker with multi-stage builds
- **CI/CD**: GitHub Actions
- **Code Quality**: ktfmt + ktlint (backend), Prettier + ESLint (frontend)
- **Git Hooks**: pre-commit framework

## Data Flow

```
Browser → SvelteKit (frontend:3000) → Ktor API (backend:8080) → SQLite
```

## Key Design Decisions

1. **Monorepo**: Single repo for atomic cross-stack changes and simplified CI/CD
2. **Kotlin/Ktor**: Static typing, coroutines, excellent IDE support
3. **SvelteKit + Skeleton**: Compiled framework with accessible UI components
4. **SQLite**: Zero-config database matching v3 for easy migration
5. **Docker**: Reproducible dev environments and production deployments
