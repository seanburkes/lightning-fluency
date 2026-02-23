# Frontend Testing Guide

## Quick Start

```bash
npm run test           # Run all unit tests
npm run test:e2e       # Run E2E tests
npm run storybook      # Start Storybook
```

## Testing Pyramid

```
    ┌─────────┐
    │   E2E   │  Few tests, high confidence
    ├─────────┤
    │  Integration  │  Component interactions
    ├─────────┤
    │   Unit   │  Many tests, fast feedback
    └─────────┘
```

## Unit Tests (Vitest)

### Store Tests

```typescript
import { get } from 'svelte/store';
import { describe, expect, it } from 'vitest';
import { settings } from '$lib/stores';

describe('settings store', () => {
	it('has default values', () => {
		expect(get(settings).theme).toBe('cerberus');
	});
});
```

### Component Tests

```typescript
import { render, screen } from '@testing-library/svelte';
import { describe, expect, it } from 'vitest';
import Header from './Header.svelte';

describe('Header', () => {
	it('renders title', () => {
		render(Header, { title: 'Test' });
		expect(screen.getByText('Test')).toBeInTheDocument();
	});
});
```

### Accessibility Tests

```typescript
import { axe } from 'jest-axe';

it('has no violations', async () => {
	const { container } = render(Header, { title: 'Test' });
	expect(await axe(container)).toHaveNoViolations();
});
```

## E2E Tests (Playwright)

```typescript
import { expect, test } from '@playwright/test';

test('home page loads', async ({ page }) => {
	await page.goto('/');
	await expect(page.locator('h1')).toBeVisible();
});
```

## API Mocking (MSW)

### Handler Organization

Handlers are organized by domain in `src/test/mocks/handlers.ts`:

```typescript
// Health endpoints
export const healthHandlers = [
  http.get('/api/health', () => HttpResponse.json({ status: 'ok' }))
];

// Language endpoints
export const languageHandlers = [
  http.get('/api/languages', () => HttpResponse.json([...languages])),
  http.get('/api/languages/:id', ({ params }) => ...)
];

// Combine all handlers
export const handlers = [...healthHandlers, ...languageHandlers, ...termHandlers];
```

### Adding a New Handler

1. Add the handler to the appropriate domain array
2. Use typed responses matching backend DTOs
3. Add delay for realistic testing: `await delay(50)`

```typescript
http.get('/api/terms', async () => {
	await delay(50);
	return HttpResponse.json([{ id: 1, text: 'hello', language_id: 1, status: 0 }]);
});
```

### Error Responses

```typescript
http.get('/api/terms/:id', ({ params }) => {
	const id = Number(params.id);
	if (id === 999) {
		return new HttpResponse(null, { status: 404 });
	}
	return HttpResponse.json({ id, text: 'hello' });
});
```

### Query Parameters

```typescript
http.get('/api/terms', ({ request }) => {
	const url = new URL(request.url);
	const languageId = url.searchParams.get('language_id');
	const status = url.searchParams.get('status');
	// Filter and return
});
```

### Environment Setup

- **Vitest**: Uses `server.ts` (Node environment)
- **Storybook/Playwright**: Uses `browser.ts` (Service Worker)

## Storybook Stories

```typescript
import type { Meta, StoryObj } from '@storybook/sveltekit';
import Header from './Header.svelte';

const meta: Meta<Header> = {
	title: 'Components/Header',
	component: Header
};

export default meta;
export const Default: StoryObj<Header> = {
	args: { title: 'Lightning Fluency' }
};
```

## Coverage Thresholds

Current threshold: 50% (lines, functions, branches, statements)

Run coverage: `npm run test:coverage`
