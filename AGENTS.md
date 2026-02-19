# Lightning Fluency - Project Knowledge Base

**Project**: Lightning Fluency  
**Type**: Language learning application exploring evidence-based educational theories  
**Origin**: Forked from [Lute v3](https://github.com/jzohrab/lute-v3)  

## Overview

Monorepo with Kotlin/Ktor backend and SvelteKit frontend. Focuses on researching language acquisition through interactive reading experiences.

## Structure

```
lightning-fluency/
├── backend/          # Kotlin/Ktor REST API (port 8080)
│   ├── src/main/kotlin/com/lute/
│   │   ├── domain/       # Entities, value objects
│   │   ├── application/  # Services, interfaces
│   │   ├── presentation/ # Routes, controllers
│   │   └── di/           # ServiceLocator (manual DI)
│   └── build.gradle.kts
├── frontend/         # SvelteKit + Skeleton UI (port 5173/3000)
│   ├── src/routes/       # SvelteKit pages
│   └── src/lib/          # API client, stores, components
├── openspec/         # OpenAPI specs and change tracking
├── docs/             # Architecture documentation
└── docker-compose.yml
```

## Technology Stack

| Layer | Technology | Version |
|-------|------------|---------|
| Backend | Kotlin | 2.1.10 |
| Backend | Ktor | 3.1.1 |
| Backend | Exposed ORM | 0.58.0 |
| Backend | SQLite | 3.47.1.0 |
| Frontend | SvelteKit | 2.x |
| Frontend | Svelte | 5.x |
| Frontend | Tailwind CSS | 4.x |
| Frontend | Skeleton UI | 4.x |

## Where to Look

| Task | Location | Notes |
|------|----------|-------|
| Backend entry | `backend/src/main/kotlin/com/lute/Application.kt` | Ktor server setup |
| Frontend entry | `frontend/src/routes/+page.svelte` | Main page |
| API client | `frontend/src/lib/api/client.ts` | fetch wrappers |
| State management | `frontend/src/lib/stores/` | Svelte stores |
| Database models | `backend/src/main/kotlin/com/lute/domain/` | Data classes |
| Services | `backend/src/main/kotlin/com/lute/application/` | Business logic |
| Routes | `backend/src/main/kotlin/com/lute/presentation/` | Ktor routing |
| Docker | `docker-compose.yml` | Full stack orchestration |
| CI/CD | `.github/workflows/ci.yml` | GitHub Actions |

## Conventions

### Backend (Kotlin)
- **ktfmt** is authoritative formatter (2-space indent)
- **ktlint** runs lint rules only (formatting rules disabled in `.editorconfig`)
- Clean architecture: domain → application → presentation → di
- Package: `com.lute`
- Manual DI via `ServiceLocator` object (not Koin runtime)

### Frontend (TypeScript/Svelte)
- **Prettier**: tabs, single quotes, no trailing commas, 100 char width
- **ESLint**: Svelte plugin enabled
- Svelte 5 runes syntax (`$props()`, `$state()`)
- Tailwind CSS v4 with `@tailwindcss/vite` plugin

### Git Hooks (pre-commit)
- Backend: ktfmt check + ktlint
- Frontend: Prettier check + ESLint
- Skip: `git commit --no-verify`

## Commands

```bash
# Backend
cd backend
./gradlew build          # Full build + checks
./gradlew run            # Dev server :8080
./gradlew ktfmtFormat    # Auto-format

# Frontend
cd frontend
npm install
npm run dev              # Dev server :5173
npm run build            # Production build
npm run check            # TypeScript check
npm run format           # Prettier
npm run lint             # ESLint

# Docker (Podman compatible)
docker-compose up        # Full stack
```

## Key Design Decisions

1. **Clean Architecture** - Backend uses layered approach with explicit boundaries
2. **Manual DI** - ServiceLocator pattern instead of runtime DI framework
3. **Podman Support** - Fully qualified image names (docker.io/library/*)
4. **Non-root Containers** - Security hardening in both Dockerfiles
5. **Named Volumes** - Data persistence via Docker volumes (not bind mounts)

## Notes

- Project renamed from "Lute" to "Lightning Fluency" to reflect research focus
- Original v3 Python code preserved in `lute/` directory (untouched)
- Health checks use `java -version` (backend) and `nc` (frontend) for Podman compatibility
- Node.js 24 is cutting-edge; may need to pin to LTS for production
