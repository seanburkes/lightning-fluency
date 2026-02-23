# Frontend Knowledge Base

**Technology**: SvelteKit 2 + Svelte 5 + TypeScript + Tailwind CSS v4 + Skeleton UI  
**Architecture**: SvelteKit file-based routing with lib/ for shared code

## Structure

```
frontend/src/
├── routes/              # SvelteKit pages (file-based routing)
│   ├── +layout.svelte   # Root layout with theme
│   ├── +page.svelte     # Home page
│   └── ...
├── lib/                 # Shared code
│   ├── api/            # API client functions
│   │   ├── client.ts
│   │   └── index.ts
│   ├── components/     # Reusable Svelte components
│   │   ├── Header.svelte
│   │   └── index.ts
│   ├── stores/         # Svelte stores (state management)
│   │   ├── settings.ts
│   │   └── index.ts
│   └── types/          # TypeScript type exports
│       └── index.ts
├── app.css             # Global styles with Tailwind/Skeleton imports
├── app.html            # HTML template
└── ...
```

## Key Features

### API Client (`lib/api/`)
- Base URL: `/api`
- `fetchHealth()`: Example API call with error handling

### State Management (`lib/stores/`)
- `settings`: App settings store (theme, fontSize, showDefinitions, autoPlayAudio)
- Actions: setTheme, setFontSize, toggleDefinitions, toggleAutoPlay

### Components (`lib/components/`)
- `Header.svelte`: Sample component with settings integration
- Props interface using Svelte 5 `$props()` rune

## Svelte 5 Runes

| Rune | Purpose |
|------|---------|
| `$props()` | Declare component props |
| `$state()` | Reactive state (in stores) |

## Styling

- **Tailwind CSS v4**: CSS-based config via `@import` (no tailwind.config.js)
- **Skeleton UI v4**: Theme system with `data-theme="cerberus"`
- Theme CSS imports in `app.css`:
  ```css
  @import 'tailwindcss';
  @import '@skeletonlabs/skeleton';
  @import '@skeletonlabs/skeleton/themes/cerberus';
  ```

## Build Commands

```bash
npm run dev        # Dev server :5173
npm run build      # Production build (adapter-static)
npm run preview    # Preview production build
npm run check      # TypeScript type checking
npm run format     # Prettier formatting
npm run lint       # ESLint
```

## Testing

### Test Commands

| Command | Description |
|---------|-------------|
| `npm run test` | Run unit tests (Vitest) |
| `npm run test:watch` | Run tests in watch mode |
| `npm run test:ui` | Vitest UI dashboard |
| `npm run test:coverage` | Run tests with coverage |
| `npm run test:e2e` | Run E2E tests (Playwright) |
| `npm run test:e2e:ui` | Playwright UI mode |
| `npm run storybook` | Start Storybook dev server |
| `npm run build-storybook` | Build static Storybook |

### Testing Stack

| Tool | Purpose |
|------|---------|
| Vitest | Unit test runner with jsdom |
| @testing-library/svelte | Component testing |
| jest-axe | Accessibility testing |
| MSW | API mocking |
| Playwright | E2E browser automation |
| Storybook | Component documentation |

### Test File Locations

| Type | Pattern |
|------|---------|
| Unit tests | `src/**/*.test.ts` |
| Component tests | `src/**/*.svelte.test.ts` |
| E2E tests | `e2e/**/*.spec.ts` |
| Stories | `src/**/*.stories.ts` |

### MSW Mocks

API handlers in `src/test/mocks/handlers.ts` mock backend responses.
- `server.ts` - Node environment (Vitest)
- `browser.ts` - Browser environment (Storybook, Playwright)

## Configuration

- **Prettier**: tabs, single quotes, no trailing commas, 100 char width
- **ESLint**: Flat config (v9+) with Svelte plugin
- **TypeScript**: Strict mode, bundler resolution
- **Adapter**: `@sveltejs/adapter-static` for SPA deployment

## Dependencies

- **@skeletonlabs/skeleton**: Core UI framework
- **@skeletonlabs/skeleton-svelte**: Svelte components
- **@tailwindcss/vite**: Tailwind v4 Vite plugin

## Notes

- Favicon served from `static/` (not lib/assets)
- Port 5173 for dev, 3000 for Docker
- SPA fallback configured in `svelte.config.js`
