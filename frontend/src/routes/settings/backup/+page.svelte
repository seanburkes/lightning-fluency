<script lang="ts">
	import { settings } from '$lib/stores';

	let backupEnabled = $state(settings.backupEnabled);
	let backupAuto = $state(settings.backupAuto);
	let backupDir = $state(settings.backupDir);
	let backupCount = $state(settings.backupCount);
	let backupWarn = $state(settings.backupWarn);
	let lastBackup = $state<string | null>(null);

	function handleToggle(field: 'backupEnabled' | 'backupAuto' | 'backupWarn') {
		if (field === 'backupEnabled') {
			backupEnabled = !backupEnabled;
			settings.setBackupEnabled(backupEnabled);
		} else if (field === 'backupAuto') {
			backupAuto = !backupAuto;
			settings.setBackupAuto(backupAuto);
		} else if (field === 'backupWarn') {
			backupWarn = !backupWarn;
			settings.setBackupWarn(backupWarn);
		}
	}

	function handleDirChange(e: Event) {
		const target = e.currentTarget as HTMLInputElement;
		backupDir = target.value;
		settings.setBackupDir(backupDir);
	}

	function handleCountChange(e: Event) {
		const target = e.currentTarget as HTMLInputElement;
		const val = parseInt(target.value);
		if (!isNaN(val) && val > 0) {
			backupCount = val;
			settings.setBackupCount(backupCount);
		}
	}
</script>

<h1>Backup Settings</h1>

<section class="setting-group">
	<h2>Backup Configuration</h2>

	<label class="toggle">
		<input type="checkbox" checked={backupEnabled} onchange={() => handleToggle('backupEnabled')} />
		<span>Enable Backup</span>
	</label>

	<label class="toggle">
		<input type="checkbox" checked={backupAuto} onchange={() => handleToggle('backupAuto')} />
		<span>Auto-backup</span>
	</label>

	<label class="toggle">
		<input type="checkbox" checked={backupWarn} onchange={() => handleToggle('backupWarn')} />
		<span>Warn before backup</span>
	</label>
</section>

<section class="setting-group">
	<h2>Backup Directory</h2>
	<input type="text" value={backupDir} oninput={handleDirChange} placeholder="/path/to/backup" />
</section>

<section class="setting-group">
	<h2>Retention</h2>
	<label>
		Number of backups to keep:
		<input type="number" min="1" max="100" value={backupCount} oninput={handleCountChange} />
	</label>
</section>

<section class="setting-group">
	<h2>Backup Status</h2>
	<p>
		{#if lastBackup}
			Last backup: {lastBackup}
		{:else}
			No backups yet
		{/if}
	</p>
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
		margin-bottom: 1rem;
		cursor: pointer;
	}

	.toggle input {
		width: 1.25rem;
		height: 1.25rem;
	}

	input[type='text'],
	input[type='number'] {
		padding: 0.5rem;
		border: 1px solid var(--theme-border-color, #d1d5db);
		border-radius: 0.375rem;
		width: 100%;
		max-width: 400px;
	}
</style>
