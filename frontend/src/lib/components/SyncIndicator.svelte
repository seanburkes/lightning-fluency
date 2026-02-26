<script lang="ts">
	import { syncStatus } from '$lib/stores';
</script>

{#if syncStatus.pendingCount > 0}
	<div class="sync-indicator" role="status">
		{#if syncStatus.syncing}
			Syncing...
		{:else}
			{syncStatus.pendingCount} pending
		{/if}
	</div>
{/if}

<style>
	.sync-indicator {
		position: fixed;
		bottom: 0.5rem;
		right: 0.5rem;
		background: var(--theme-container-surface, #1e1e2e);
		border: 1px solid var(--theme-border, #333);
		padding: 0.25rem 0.5rem;
		border-radius: 0.25rem;
		font-size: 0.75rem;
		z-index: 800;
	}
</style>
