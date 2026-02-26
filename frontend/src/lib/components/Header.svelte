<script lang="ts">
	import { settings } from '$lib/stores';

	interface Props {
		title: string;
	}

	let { title }: Props = $props();

	function handleFontSizeChange(e: Event) {
		const target = e.currentTarget as HTMLInputElement;
		const val = parseInt(target.value);
		if (!isNaN(val) && val >= 12 && val <= 24) {
			settings.setFontSize(val);
		}
	}
</script>

<header class="header">
	<div class="left">
		<h1 class="h1">{title}</h1>
		<nav class="nav">
			<!-- eslint-disable-next-line svelte/no-navigation-without-resolve -->
			<a href="/">Home</a>
			<!-- eslint-disable-next-line svelte/no-navigation-without-resolve -->
			<a href="/books">Books</a>
			<!-- eslint-disable-next-line svelte/no-navigation-without-resolve -->
			<a href="/terms">Terms</a>
		</nav>
	</div>
	<div class="right">
		<!-- eslint-disable-next-line svelte/no-navigation-without-resolve -->
		<a href="/settings" class="settings-link">Settings</a>
		<label>
			Font Size:
			<input
				type="number"
				min="12"
				max="24"
				value={settings.fontSize}
				oninput={handleFontSizeChange}
			/>
		</label>
	</div>
</header>

<style>
	.header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 1rem;
		border-bottom: 1px solid var(--theme-border-color, #e5e7eb);
	}
	.left {
		display: flex;
		align-items: center;
		gap: 2rem;
	}
	.nav {
		display: flex;
		gap: 1rem;
	}
	.nav a {
		color: var(--theme-on-surface, #374151);
		text-decoration: none;
	}
	.nav a:hover {
		text-decoration: underline;
	}
	.right {
		display: flex;
		align-items: center;
		gap: 1rem;
	}
	.settings-link {
		padding: 0.5rem 1rem;
		background: var(--theme-primary, #3b82f6);
		color: white;
		text-decoration: none;
		border-radius: 0.375rem;
	}
	.settings-link:hover {
		opacity: 0.9;
	}
</style>
