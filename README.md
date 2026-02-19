# Lightning Fluency

Lightning Fluency is a language learning application designed to explore evidence-based educational theories of language acquisition through interactive reading experiences.

> **Note:** This project began as a fork of the original [Lute v3](https://github.com/jzohrab/lute-v3) repository. While Lute v3 (Python/Flask) focused on traditional vocabulary acquisition through reading, Lightning Fluency (v4, Kotlin/SvelteKit) extends this foundation to research and implement pedagogical approaches grounded in contemporary language acquisition theory.

## v4 Development

### Prerequisites

- Java 21 (OpenJDK)
- Node.js 24+
- npm 11+
- (Optional) Docker and Docker Compose

### Getting Started

```bash
# Backend
cd backend
./gradlew build        # Build and run all checks
./gradlew test         # Run tests only
./gradlew run          # Start dev server on :8080

# Frontend
cd frontend
npm install            # Install dependencies
npm run dev            # Start dev server on :5173
npm run build          # Production build
npm run check          # TypeScript type checking
```

### Build Commands

| Command | Location | Description |
|---------|----------|-------------|
| `./gradlew build` | backend/ | Full build with lint checks |
| `./gradlew test` | backend/ | Run backend tests |
| `./gradlew ktfmtFormat` | backend/ | Auto-format Kotlin code |
| `./gradlew ktfmtCheck` | backend/ | Check Kotlin formatting |
| `./gradlew ktlintCheck` | backend/ | Lint Kotlin code |
| `npm run dev` | frontend/ | Start dev server |
| `npm run build` | frontend/ | Production build |
| `npm run check` | frontend/ | TypeScript type check |
| `npm run format` | frontend/ | Format with Prettier |
| `npm run lint` | frontend/ | Lint with ESLint |

### Docker

```bash
# Start full stack
docker-compose up

# Backend only
docker-compose up backend

# Rebuild after changes
docker-compose up --build
```

- Backend: http://localhost:8080
- Frontend: http://localhost:5173 (dev) or http://localhost:3000 (Docker)
- Health check: http://localhost:8080/api/health

### Code Quality

Git hooks run automatically on commit via pre-commit:
- Backend: ktfmt format check + ktlint lint
- Frontend: Prettier format check + ESLint lint

To skip hooks: `git commit --no-verify`

### Project Structure

See [docs/architecture.md](docs/architecture.md) for detailed architecture documentation.
