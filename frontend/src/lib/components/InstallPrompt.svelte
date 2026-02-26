<script lang="ts">
	import { onMount } from 'svelte';

	interface BeforeInstallPromptEvent extends Event {
		prompt(): Promise<void>;
		userChoice: Promise<{ outcome: 'accepted' | 'dismissed' }>;
	}

	let deferredPrompt = $state<BeforeInstallPromptEvent | null>(null);
	let showBanner = $state(false);
	let dismissed = $state(false);

	onMount(() => {
		function handleBeforeInstallPrompt(e: Event) {
			e.preventDefault();
			deferredPrompt = e as BeforeInstallPromptEvent;
			if (!dismissed) {
				showBanner = true;
			}
		}

		function handleAppInstalled() {
			deferredPrompt = null;
			showBanner = false;
		}

		window.addEventListener('beforeinstallprompt', handleBeforeInstallPrompt);
		window.addEventListener('appinstalled', handleAppInstalled);

		return () => {
			window.removeEventListener('beforeinstallprompt', handleBeforeInstallPrompt);
			window.removeEventListener('appinstalled', handleAppInstalled);
		};
	});

	async function handleInstall() {
		if (!deferredPrompt) return;
		try {
			await deferredPrompt.prompt();
			const { outcome } = await deferredPrompt.userChoice;
			if (outcome === 'accepted') {
				showBanner = false;
			}
		} catch (error) {
			console.error('Install prompt failed:', error);
		}
		deferredPrompt = null;
	}

	function handleDismiss() {
		showBanner = false;
		dismissed = true;
	}
</script>

{#if showBanner}
	<div class="install-banner" role="alert">
		<div class="install-content">
			<span class="install-text">Install Lightning Fluency for offline reading</span>
			<div class="install-actions">
				<button class="btn btn-primary btn-sm" onclick={handleInstall}>Install</button>
				<button class="btn btn-ghost btn-sm" onclick={handleDismiss}>Dismiss</button>
			</div>
		</div>
	</div>
{/if}

<style>
	.install-banner {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		background: var(--theme-container-surface, #1e1e2e);
		border-top: 1px solid var(--theme-border, #333);
		padding: 0.75rem 1rem;
		z-index: 900;
		box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.2);
	}
	.install-content {
		display: flex;
		align-items: center;
		justify-content: space-between;
		max-width: 48rem;
		margin: 0 auto;
		gap: 1rem;
	}
	.install-text {
		font-size: 0.875rem;
	}
	.install-actions {
		display: flex;
		gap: 0.5rem;
		flex-shrink: 0;
	}
	.btn-sm {
		padding: 0.25rem 0.75rem;
		font-size: 0.875rem;
	}
	.btn-ghost {
		background: transparent;
		border: none;
		color: var(--theme-text-muted, #999);
		cursor: pointer;
	}
	.btn-ghost:hover {
		color: var(--theme-text, #fff);
	}
</style>
