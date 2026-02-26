<script lang="ts">
	import { settings } from '$lib/stores';

	let useAnkiConnect = $state(settings.useAnkiConnect);
	let ankiConnectUrl = $state(settings.ankiConnectUrl);
	let testing = $state(false);
	let testResult = $state<string | null>(null);

	function handleToggle() {
		useAnkiConnect = !useAnkiConnect;
		settings.setUseAnkiConnect(useAnkiConnect);
	}

	function handleUrlChange(e: Event) {
		const target = e.currentTarget as HTMLInputElement;
		ankiConnectUrl = target.value;
		settings.setAnkiConnectUrl(ankiConnectUrl);
	}

	async function testConnection() {
		testing = true;
		testResult = null;
		try {
			await fetch(ankiConnectUrl, {
				method: 'GET'
			});
			testResult = 'Connection successful!';
		} catch (e) {
			testResult = `Connection failed: ${e}`;
		} finally {
			testing = false;
		}
	}
</script>

<h1>Anki Settings</h1>

<section class="setting-group">
	<h2>AnkiConnect Configuration</h2>

	<label class="toggle">
		<input type="checkbox" checked={useAnkiConnect} onchange={handleToggle} />
		<span>Enable AnkiConnect</span>
	</label>

	<p class="help-text">
		AnkiConnect allows you to send terms directly to Anki. Make sure Anki is running with the
		AnkiConnect addon installed.
	</p>
</section>

{#if useAnkiConnect}
	<section class="setting-group">
		<h2>AnkiConnect URL</h2>
		<input
			type="text"
			value={ankiConnectUrl}
			oninput={handleUrlChange}
			placeholder="http://localhost:8765"
		/>
	</section>

	<section class="setting-group">
		<h2>Test Connection</h2>
		<button onclick={testConnection} disabled={testing}>
			{testing ? 'Testing...' : 'Test Connection'}
		</button>
		{#if testResult}
			<p class="result" class:success={testResult.includes('successful')}>
				{testResult}
			</p>
		{/if}
	</section>
{/if}

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
		margin-bottom: 1rem;
	}

	.toggle input {
		width: 1.25rem;
		height: 1.25rem;
	}

	.help-text {
		color: var(--theme-muted, #6b7280);
		font-size: 0.875rem;
	}

	input[type='text'] {
		padding: 0.5rem;
		border: 1px solid var(--theme-border-color, #d1d5db);
		border-radius: 0.375rem;
		width: 100%;
		max-width: 400px;
	}

	button {
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

	button:disabled {
		opacity: 0.5;
		cursor: not-allowed;
	}

	.result {
		margin-top: 0.75rem;
		padding: 0.5rem;
		border-radius: 0.375rem;
		background: #fef2f2;
		color: #dc2626;
	}

	.result.success {
		background: #f0fdf4;
		color: #16a34a;
	}
</style>
