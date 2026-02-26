<script lang="ts">
	import { settings } from '$lib/stores';

	let selectedTheme = $state(settings.theme);
	let customCss = $state(settings.customCss);

	const themes = [
		{ name: 'cerberus', label: 'Cerberus (Default)' },
		{ name: 'skeleton', label: 'Skeleton' },
		{ name: 'wintry', label: 'Wintry' }
	];

	function handleThemeChange(e: Event) {
		const target = e.currentTarget as HTMLSelectElement;
		selectedTheme = target.value;
		settings.setTheme(selectedTheme);
		document.documentElement.setAttribute('data-theme', selectedTheme);
	}

	function handleCssChange(e: Event) {
		const target = e.currentTarget as HTMLTextAreaElement;
		customCss = target.value;
		settings.setCustomCss(customCss);
	}

	function applyCustomCss() {
		let existing = document.getElementById('custom-css');
		if (!existing) {
			existing = document.createElement('style');
			existing.id = 'custom-css';
			document.head.appendChild(existing);
		}
		existing.textContent = customCss;
	}
</script>

<h1>Theme Settings</h1>

<section class="setting-group">
	<h2>Theme</h2>
	<label>
		Select theme:
		<select value={selectedTheme} onchange={handleThemeChange}>
			{#each themes as theme (theme.name)}
				<option value={theme.name}>{theme.label}</option>
			{/each}
		</select>
	</label>
</section>

<section class="setting-group">
	<h2>Custom CSS</h2>
	<p>Add custom CSS to customize the appearance.</p>
	<textarea
		bind:value={customCss}
		oninput={handleCssChange}
		rows="10"
		placeholder="/* Your custom CSS here */"
	></textarea>
	<button onclick={applyCustomCss}>Apply Custom CSS</button>
</section>

<section class="setting-group">
	<h2>Preview</h2>
	<div class="preview-box">
		<p>This is how your theme looks.</p>
		<button>Sample Button</button>
	</div>
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

	label {
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
	}

	select {
		padding: 0.5rem;
		border: 1px solid var(--theme-border-color, #d1d5db);
		border-radius: 0.375rem;
		background: var(--theme-surface, #fff);
		max-width: 300px;
	}

	textarea {
		width: 100%;
		max-width: 600px;
		padding: 0.75rem;
		border: 1px solid var(--theme-border-color, #d1d5db);
		border-radius: 0.375rem;
		font-family: monospace;
		font-size: 0.875rem;
		resize: vertical;
	}

	button {
		margin-top: 0.75rem;
		padding: 0.5rem 1rem;
		background: var(--theme-primary, #3b82f6);
		color: white;
		border: none;
		border-radius: 0.375rem;
		cursor: pointer;
	}

	button:hover {
		opacity: 0.9;
	}

	.preview-box {
		padding: 1.5rem;
		border: 1px solid var(--theme-border-color, #e5e7eb);
		border-radius: 0.375rem;
		background: var(--theme-surface, #fff);
	}
</style>
