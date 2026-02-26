<script lang="ts">
	import { api, type HotkeyConfig } from '$lib/api';
	import { onMount } from 'svelte';

	let hotkeys = $state<HotkeyConfig[]>([]);
	let conflicts = $state<string[]>([]);
	let capturing = $state<string | null>(null);

	onMount(async () => {
		try {
			hotkeys = await api.hotkeys.getAll();
		} catch (e) {
			console.error('Failed to load hotkeys:', e);
		}
	});

	function detectConflicts() {
		const seen: Record<string, string> = {};
		const conflictList: string[] = [];
		for (const hk of hotkeys) {
			const key = hk.hotkey.toLowerCase();
			if (key in seen) {
				conflictList.push(`${hk.action} conflicts with ${seen[key]}`);
			} else {
				seen[key] = hk.action;
			}
		}
		conflicts = conflictList;
	}

	function handleHotkeyChange(action: string, newHotkey: string) {
		hotkeys = hotkeys.map((hk) => (hk.action === action ? { ...hk, hotkey: newHotkey } : hk));
		detectConflicts();
	}

	async function saveHotkeys() {
		try {
			await api.hotkeys.set(hotkeys);
		} catch (e) {
			console.error('Failed to save hotkeys:', e);
		}
	}

	async function resetToDefaults() {
		try {
			await api.hotkeys.reset();
			hotkeys = await api.hotkeys.getAll();
		} catch (e) {
			console.error('Failed to reset hotkeys:', e);
		}
	}

	function startCapture(action: string) {
		capturing = action;
	}

	function handleKeyCapture(e: KeyboardEvent, action: string) {
		if (!capturing) return;
		e.preventDefault();
		const parts: string[] = [];
		if (e.ctrlKey) parts.push('ctrl');
		if (e.altKey) parts.push('alt');
		if (e.shiftKey) parts.push('shift');
		if (e.metaKey) parts.push('meta');
		if (e.key && !['Control', 'Alt', 'Shift', 'Meta'].includes(e.key)) {
			parts.push(e.key.toLowerCase());
		}
		const hotkey = parts.join('+');
		handleHotkeyChange(action, hotkey);
		capturing = null;
	}
</script>

<svelte:window onkeydown={(e) => capturing && handleKeyCapture(e, capturing)} />

<h1>Keyboard Shortcuts</h1>

{#if conflicts.length > 0}
	<div class="warning">
		<p>⚠️ Conflicts detected:</p>
		<ul>
			{#each conflicts as conflict (conflict)}
				<li>{conflict}</li>
			{/each}
		</ul>
	</div>
{/if}

<section class="setting-group">
	<h2>Configure Hotkeys</h2>
	<p>Click on a hotkey field and press your desired key combination.</p>

	<table class="hotkey-table">
		<thead>
			<tr>
				<th>Action</th>
				<th>Hotkey</th>
			</tr>
		</thead>
		<tbody>
			{#each hotkeys as hk (hk.action)}
				<tr>
					<td>{hk.description}</td>
					<td>
						{#if capturing === hk.action}
							<span class="capturing">Press keys...</span>
						{:else}
							<button class="hotkey-btn" onclick={() => startCapture(hk.action)}>
								{hk.hotkey}
							</button>
						{/if}
					</td>
				</tr>
			{/each}
		</tbody>
	</table>
</section>

<section class="actions">
	<button onclick={saveHotkeys}>Save Hotkeys</button>
	<button class="secondary" onclick={resetToDefaults}>Reset to Defaults</button>
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

	.warning {
		padding: 1rem;
		background: #fef3c7;
		border: 1px solid #f59e0b;
		border-radius: 0.375rem;
		margin-bottom: 1.5rem;
	}

	.warning ul {
		margin: 0.5rem 0 0 1.5rem;
	}

	.setting-group {
		margin-bottom: 2rem;
		padding: 1.5rem;
		border: 1px solid var(--theme-border-color, #e5e7eb);
		border-radius: 0.5rem;
	}

	.hotkey-table {
		width: 100%;
		border-collapse: collapse;
	}

	.hotkey-table th,
	.hotkey-table td {
		text-align: left;
		padding: 0.75rem;
		border-bottom: 1px solid var(--theme-border-color, #e5e7eb);
	}

	.hotkey-btn {
		padding: 0.5rem 1rem;
		border: 1px solid var(--theme-border-color, #d1d5db);
		border-radius: 0.375rem;
		background: var(--theme-surface, #fff);
		cursor: pointer;
		font-family: monospace;
	}

	.hotkey-btn:hover {
		background: var(--theme-hover, #f3f4f6);
	}

	.capturing {
		color: var(--theme-primary, #3b82f6);
		font-style: italic;
	}

	.actions {
		display: flex;
		gap: 1rem;
	}

	button {
		padding: 0.5rem 1rem;
		background: var(--theme-primary, #3b82f6);
		color: white;
		border: none;
		border-radius: 0.375rem;
		cursor: pointer;
	}

	button.secondary {
		background: var(--theme-surface, #6b7280);
	}

	button:hover {
		opacity: 0.9;
	}
</style>
