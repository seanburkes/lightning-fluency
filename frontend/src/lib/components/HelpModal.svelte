<script lang="ts">
	import { keyboard } from '$lib/stores';

	interface Props {
		shortcuts: Array<{ key: string; description: string; ctrl?: boolean; shift?: boolean }>;
	}

	let { shortcuts }: Props = $props();

	function handleKeydown(event: KeyboardEvent) {
		if (event.key === 'Escape') {
			keyboard.closeHelp();
		}
	}

	function formatKey(key: string, ctrl?: boolean, shift?: boolean): string {
		const parts: string[] = [];
		if (ctrl) parts.push('Ctrl');
		if (shift) parts.push('Shift');
		parts.push(key.toUpperCase());
		return parts.join('+');
	}
</script>

<svelte:window onkeydown={handleKeydown} />

{#if $keyboard.helpOpen}
	<div
		class="modal-backdrop"
		onclick={() => keyboard.closeHelp()}
		onkeydown={(e) => e.key === 'Enter' && keyboard.closeHelp()}
		role="button"
		tabindex="0"
	>
		<div
			class="modal"
			onclick={(e) => e.stopPropagation()}
			onkeydown={(e) => e.stopPropagation()}
			role="dialog"
			aria-modal="true"
			aria-labelledby="help-title"
			tabindex="-1"
		>
			<h2 id="help-title" class="h2">Keyboard Shortcuts</h2>
			<table class="shortcuts-table">
				<thead>
					<tr>
						<th>Key</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
					{#each shortcuts as shortcut (shortcut.key)}
						<tr>
							<td><kbd>{formatKey(shortcut.key, shortcut.ctrl, shortcut.shift)}</kbd></td>
							<td>{shortcut.description}</td>
						</tr>
					{/each}
				</tbody>
			</table>
			<button class="btn btn-primary" onclick={() => keyboard.closeHelp()}>Close</button>
		</div>
	</div>
{/if}

<style>
	.modal-backdrop {
		position: fixed;
		inset: 0;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 1000;
	}
	.modal {
		background: var(--theme-container-surface);
		padding: 1.5rem;
		border-radius: 0.5rem;
		max-width: 500px;
		width: 90%;
		max-height: 80vh;
		overflow-y: auto;
	}
	.h2 {
		margin-bottom: 1rem;
	}
	.shortcuts-table {
		width: 100%;
		border-collapse: collapse;
		margin-bottom: 1rem;
	}
	.shortcuts-table th,
	.shortcuts-table td {
		padding: 0.5rem;
		text-align: left;
		border-bottom: 1px solid var(--theme-border);
	}
	.shortcuts-table th {
		font-weight: 600;
	}
	kbd {
		background: var(--theme-surface-2);
		padding: 0.25rem 0.5rem;
		border-radius: 0.25rem;
		font-family: monospace;
		font-size: 0.875rem;
	}
	.btn {
		margin-top: 1rem;
	}
</style>
