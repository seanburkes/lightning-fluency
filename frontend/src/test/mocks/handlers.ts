import { http, HttpResponse, delay } from 'msw';
import type { HealthResponse } from '$lib/api/client';

export const API_BASE = '/api';

export const healthHandlers = [
	http.get(`${API_BASE}/health`, async () => {
		await delay(50);
		return HttpResponse.json<HealthResponse>({ status: 'ok' });
	})
];

export const languageHandlers = [
	http.get(`${API_BASE}/languages`, async () => {
		await delay(50);
		return HttpResponse.json([
			{ id: 1, name: 'English', parser_type: 'spacedel' },
			{ id: 2, name: 'Spanish', parser_type: 'spacedel' }
		]);
	}),
	http.get(`${API_BASE}/languages/:id`, async ({ params }) => {
		await delay(50);
		const id = Number(params.id);
		if (id === 1) {
			return HttpResponse.json({ id: 1, name: 'English', parser_type: 'spacedel' });
		}
		return new HttpResponse(null, { status: 404 });
	})
];

export const termHandlers = [
	http.get(`${API_BASE}/terms`, async ({ request }) => {
		await delay(50);
		const url = new URL(request.url);
		const languageId = url.searchParams.get('language_id');
		const status = url.searchParams.get('status');

		let terms = [
			{
				id: 1,
				text: 'hello',
				language_id: 1,
				status: 0,
				translation: 'greeting',
				tags: ['common'],
				parents: [],
				children_count: 0
			},
			{
				id: 2,
				text: 'world',
				language_id: 1,
				status: 1,
				translation: 'earth',
				tags: ['common'],
				parents: [],
				children_count: 0
			}
		];

		if (languageId) {
			terms = terms.filter((t) => t.language_id === Number(languageId));
		}
		if (status !== null) {
			terms = terms.filter((t) => t.status === Number(status));
		}

		return HttpResponse.json(terms);
	}),
	http.get(`${API_BASE}/terms/:id`, async ({ params }) => {
		await delay(50);
		const id = Number(params.id);
		if (id === 1) {
			return HttpResponse.json({
				id: 1,
				text: 'hello',
				language_id: 1,
				status: 0,
				translation: 'greeting',
				tags: ['common'],
				parents: [],
				children_count: 0
			});
		}
		return new HttpResponse(null, { status: 404 });
	})
];

export const handlers = [...healthHandlers, ...languageHandlers, ...termHandlers];
