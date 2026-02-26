import { sveltekit } from '@sveltejs/kit/vite';
import tailwindcss from '@tailwindcss/vite';
import { SvelteKitPWA } from '@vite-pwa/sveltekit';
import { defineConfig } from 'vite';

export default defineConfig({
	plugins: [
		tailwindcss(),
		sveltekit(),
		SvelteKitPWA({
			strategies: 'injectManifest',
			srcDir: 'src',
			filename: 'service-worker.ts',
			registerType: 'autoUpdate',
			injectRegister: false,
			manifest: {
				name: 'Lightning Fluency',
				short_name: 'Fluency',
				description: 'Learn languages through reading',
				theme_color: '#4f46e5',
				background_color: '#ffffff',
				display: 'standalone',
				start_url: '/',
				icons: [
					{
						src: '/favicon-192.png',
						sizes: '192x192',
						type: 'image/png'
					},
					{
						src: '/favicon-512.png',
						sizes: '512x512',
						type: 'image/png'
					},
					{
						src: '/favicon-512.png',
						sizes: '512x512',
						type: 'image/png',
						purpose: 'maskable'
					}
				]
			},
			injectManifest: {
				globPatterns: ['client/**/*.{js,css,ico,png,svg,webp,webmanifest}', 'prerendered/**/*.html']
			},
			devOptions: {
				enabled: false
			},
			kit: {
				adapterFallback: 'index.html',
				spa: true
			}
		})
	]
});
