<script lang="ts">
	import '../../app.css';

	interface Props {
		children?: import('svelte').Snippet;
	}

	let { children }: Props = $props();
</script>

<div class="settings-layout">
	<nav class="settings-nav">
		<h2 class="nav-title">Settings</h2>
		<ul class="nav-list">
			<!-- eslint-disable-next-line svelte/no-navigation-without-resolve -->
			<li><a href="/settings/theme">Theme</a></li>
			<!-- eslint-disable-next-line svelte/no-navigation-without-resolve -->
			<li><a href="/settings/backup">Backup</a></li>
			<!-- eslint-disable-next-line svelte/no-navigation-without-resolve -->
			<li><a href="/settings/hotkeys">Keyboard Shortcuts</a></li>
			<!-- eslint-disable-next-line svelte/no-navigation-without-resolve -->
			<li><a href="/settings/anki">Anki</a></li>
			<!-- eslint-disable-next-line svelte/no-navigation-without-resolve -->
			<li><a href="/settings/reading">Reading</a></li>
		</ul>
	</nav>
	<main class="settings-content">
		{@render children?.()}
	</main>
</div>

<style>
	.settings-layout {
		display: flex;
		min-height: 100vh;
	}

	.settings-nav {
		width: 240px;
		padding: 1.5rem;
		border-right: 1px solid var(--theme-border-color, #e5e7eb);
		background: var(--theme-surface, #f9fafb);
	}

	.nav-title {
		font-size: 1.25rem;
		font-weight: 600;
		margin-bottom: 1rem;
	}

	.nav-list {
		list-style: none;
		padding: 0;
		margin: 0;
	}

	.nav-list li {
		margin-bottom: 0.5rem;
	}

	.nav-list a {
		display: block;
		padding: 0.5rem 0.75rem;
		color: var(--theme-on-surface, #374151);
		text-decoration: none;
		border-radius: 0.375rem;
		transition: background-color 0.15s;
	}

	.nav-list a:hover {
		background: var(--theme-hover, #e5e7eb);
	}

	.settings-content {
		flex: 1;
		padding: 2rem;
	}
</style>
