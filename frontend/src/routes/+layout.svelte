<script lang="ts">
	import { onMount } from 'svelte';
	import { pwaInfo } from 'virtual:pwa-info';
	import '../app.css';
	import { keyboard, navigation, termPopup, syncStatus, settings } from '$lib/stores';
	import { HelpModal, InstallPrompt, SyncIndicator } from '$lib/components';

	let { children } = $props();

	let webManifestLink = $derived(pwaInfo ? pwaInfo.webManifest.linkTag : '');

	onMount(() => {
		settings.initialize();
	});

	function handleOnline() {
		syncStatus.setOnline(true);
	}

	function handleOffline() {
		syncStatus.setOnline(false);
	}

	onMount(() => {
		if (pwaInfo) {
			import('virtual:pwa-register').then(({ registerSW }) => {
				registerSW({
					immediate: true,
					onRegisteredSW(swUrl, registration) {
						if (registration) {
							setInterval(
								() => {
									registration.update();
								},
								60 * 60 * 1000
							);
						}
					}
				});
			});
		}

		window.addEventListener('online', handleOnline);
		window.addEventListener('offline', handleOffline);
		syncStatus.refreshCount().catch(() => {
			// IndexedDB may not be available
		});

		return () => {
			window.removeEventListener('online', handleOnline);
			window.removeEventListener('offline', handleOffline);
		};
	});

	const shortcuts = [
		{ key: 'ArrowRight', description: 'Next page' },
		{ key: 'ArrowLeft', description: 'Previous page' },
		{ key: ' ', description: 'Next page (space)' },
		{ key: 'Enter', description: 'Open term popup' },
		{ key: 'Escape', description: 'Close term popup / Close modal' },
		{ key: '?', description: 'Show this help' }
	];

	function handleGlobalKeydown(event: KeyboardEvent) {
		keyboard.handleKeydown(event);
	}

	keyboard.registerShortcut({
		key: '?',
		description: 'Show keyboard shortcuts help',
		action: () => keyboard.toggleHelp()
	});

	keyboard.registerShortcut({
		key: 'ArrowRight',
		description: 'Next page',
		action: () => navigation.nextPage()
	});

	keyboard.registerShortcut({
		key: 'ArrowLeft',
		description: 'Previous page',
		action: () => navigation.prevPage()
	});

	keyboard.registerShortcut({
		key: ' ',
		description: 'Next page',
		action: () => navigation.nextPage(),
		preventDefault: false
	});

	keyboard.registerShortcut({
		key: 'Escape',
		description: 'Close modal / Clear selection',
		action: () => {
			keyboard.closeHelp();
			termPopup.close();
		}
	});

	keyboard.registerShortcut({
		key: 'Enter',
		description: 'Open term popup',
		action: () => {
			// Will be connected to reading view when implemented
		}
	});
</script>

<svelte:window onkeydown={handleGlobalKeydown} />

<svelte:head>
	<link rel="icon" href="/favicon.svg" />
	<!-- eslint-disable-next-line svelte/no-at-html-tags -->
	{@html webManifestLink}
</svelte:head>

{@render children()}
<HelpModal {shortcuts} />
<InstallPrompt />
<SyncIndicator />
