<script lang="ts">
	import { settings } from '$lib/stores';
	import { api } from '$lib/api';
	import { onMount } from 'svelte';

	let showHighlights = $state(settings.showHighlights);
	let focusMode = $state(settings.focusMode);
	let tapBehavior = $state(settings.tapBehavior);
	let defaultLanguageId = $state(settings.defaultLanguageId);
	let languages = $state<{ id: number; name: string }[]>([]);

	onMount(async () => {
		try {
			const langs = await api.languages.getAll();
			languages = langs.map((l) => ({ id: l.id, name: l.name }));
		} catch (e) {
			console.error('Failed to load languages:', e);
		}
	});

	function handleToggle(field: 'showHighlights' | 'focusMode') {
		if (field === 'showHighlights') {
			showHighlights = !showHighlights;
			settings.setShowHighlights(showHighlights);
		} else {
			focusMode = !focusMode;
			settings.setFocusMode(focusMode);
		}
	}

	function handleTapBehaviorChange(e: Event) {
		const target = e.currentTarget as HTMLSelectElement;
		tapBehavior = target.value;
		settings.setTapBehavior(tapBehavior);
	}

	function handleLanguageChange(e: Event) {
		const target = e.currentTarget as HTMLSelectElement;
		const val = target.value;
		defaultLanguageId = val ? parseInt(val) : null;
		settings.setDefaultLanguageId(defaultLanguageId);
	}

	const tapBehaviorOptions = [
		{ value: 'popup', label: 'Show popup' },
		{ value: 'define', label: 'Define immediately' },
		{ value: 'ignore', label: 'Ignore tap' }
	];
</script>

<h1>Reading Settings</h1>

<section class="setting-group">
	<h2>Display</h2>

	<label class="toggle">
		<input
			type="checkbox"
			checked={showHighlights}
			onchange={() => handleToggle('showHighlights')}
		/>
		<span>Show highlights</span>
	</label>
	<p class="help-text">Highlight terms that exist in your vocabulary.</p>

	<label class="toggle">
		<input type="checkbox" checked={focusMode} onchange={() => handleToggle('focusMode')} />
		<span>Focus mode</span>
	</label>
	<p class="help-text">Hide the sidebar and other UI elements while reading.</p>
</section>

<section class="setting-group">
	<h2>Tap Behavior</h2>
	<select value={tapBehavior} onchange={handleTapBehaviorChange}>
		{#each tapBehaviorOptions as option (option.value)}
			<option value={option.value}>{option.label}</option>
		{/each}
	</select>
	<p class="help-text">What happens when you tap/click on a word.</p>
</section>

<section class="setting-group">
	<h2>Default Language</h2>
	<select value={defaultLanguageId ?? ''} onchange={handleLanguageChange}>
		<option value="">Select a language...</option>
		{#each languages as lang (lang.id)}
			<option value={lang.id}>{lang.name}</option>
		{/each}
	</select>
	<p class="help-text">Default language for new books and terms.</p>
</section>

<style>
	h1 {
		font-size: 1.5rem;
		font-weight: 600;
		margin-bottom: 1.5rem;
	}

	h2 {
		font-size: 1.125rem;
		font-weight: 500;
		margin-bottom: 0.75rem;
	}

	.setting-group {
		margin-bottom: 2rem;
		padding: 1.5rem;
		border: 1px solid var(--theme-border-color, #e5e7eb);
		border-radius: 0.5rem;
	}

	.toggle {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		cursor: pointer;
		margin-bottom: 0.5rem;
	}

	.toggle input {
		width: 1.25rem;
		height: 1.25rem;
	}

	.help-text {
		color: var(--theme-muted, #6b7280);
		font-size: 0.875rem;
		margin-bottom: 1rem;
	}

	select {
		padding: 0.5rem;
		border: 1px solid var(--theme-border-color, #d1d5db);
		border-radius: 0.375rem;
		background: var(--theme-surface, #fff);
		width: 100%;
		max-width: 300px;
	}
</style>
