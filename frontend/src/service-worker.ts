/// <reference lib="webworker" />

import { cleanupOutdatedCaches, precacheAndRoute } from 'workbox-precaching';
import { CacheFirst, NetworkFirst, StaleWhileRevalidate } from 'workbox-strategies';
import { ExpirationPlugin } from 'workbox-expiration';
import { registerRoute } from 'workbox-routing';
import { CacheableResponsePlugin } from 'workbox-cacheable-response';

declare let self: ServiceWorkerGlobalScope;

cleanupOutdatedCaches();
precacheAndRoute(self.__WB_MANIFEST);

registerRoute(
	({ request }) => request.destination === 'script' || request.destination === 'style',
	new CacheFirst({
		cacheName: 'static',
		plugins: [
			new CacheableResponsePlugin({ statuses: [200] }),
			new ExpirationPlugin({ maxEntries: 60, maxAgeSeconds: 30 * 24 * 60 * 60 })
		]
	})
);

registerRoute(
	({ request }) => request.destination === 'image',
	new CacheFirst({
		cacheName: 'images',
		plugins: [
			new CacheableResponsePlugin({ statuses: [200] }),
			new ExpirationPlugin({ maxEntries: 100, maxAgeSeconds: 30 * 24 * 60 * 60 })
		]
	})
);

registerRoute(
	({ url }) => url.pathname.startsWith('/api/books'),
	new StaleWhileRevalidate({
		cacheName: 'api-books',
		plugins: [
			new CacheableResponsePlugin({ statuses: [0, 200] }),
			new ExpirationPlugin({ maxEntries: 50, maxAgeSeconds: 7 * 24 * 60 * 60 })
		]
	})
);

registerRoute(
	({ url }) => url.pathname.startsWith('/api/terms'),
	new NetworkFirst({
		cacheName: 'api-terms',
		plugins: [
			new CacheableResponsePlugin({ statuses: [0, 200] }),
			new ExpirationPlugin({ maxEntries: 200, maxAgeSeconds: 7 * 24 * 60 * 60 })
		],
		networkTimeoutSeconds: 3
	})
);

registerRoute(
	({ url }) => url.pathname.startsWith('/api/languages'),
	new StaleWhileRevalidate({
		cacheName: 'api-languages',
		plugins: [
			new CacheableResponsePlugin({ statuses: [0, 200] }),
			new ExpirationPlugin({ maxEntries: 20, maxAgeSeconds: 30 * 24 * 60 * 60 })
		]
	})
);

self.addEventListener('message', (event) => {
	if (event.data && event.data.type === 'SKIP_WAITING') {
		self.skipWaiting();
	}
});
