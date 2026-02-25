<script lang="ts">
	import { api } from '$lib/api/client';

	interface Props {
		bookId: number;
		audioFilename?: string | null;
	}

	let { bookId, audioFilename }: Props = $props();

	let audioElement: HTMLAudioElement | null = $state(null);
	let isPlaying = $state(false);
	let currentTime = $state(0);
	let duration = $state(0);
	let isUploading = $state(false);
	let errorMessage: string | null = $state(null);

	async function handleFileUpload(e: Event) {
		const target = e.currentTarget as HTMLInputElement;
		const file = target.files?.[0];
		if (!file) return;

		errorMessage = null;
		isUploading = true;
		try {
			const data = await api.books.uploadAudio(bookId, file);
			audioFilename = data.filename;
			loadAudio();
		} catch (err) {
			errorMessage = err instanceof Error ? err.message : 'Upload failed. Please try again.';
			console.error('Upload error:', err);
		} finally {
			isUploading = false;
		}
	}

	function loadAudio() {
		if (!audioElement || !audioFilename) return;
		audioElement.src = api.books.getAudioUrl(bookId);
		audioElement.load();
	}

	function togglePlay() {
		if (!audioElement) return;
		if (isPlaying) {
			audioElement.pause();
		} else {
			audioElement.play();
		}
	}

	function handleTimeUpdate() {
		if (audioElement) {
			currentTime = audioElement.currentTime;
		}
	}

	function handleLoadedMetadata() {
		if (audioElement) {
			duration = audioElement.duration;
		}
	}

	function handleEnded() {
		isPlaying = false;
		currentTime = 0;
	}

	function handlePlay() {
		isPlaying = true;
	}

	function handlePause() {
		isPlaying = false;
	}

	function formatTime(seconds: number): string {
		const mins = Math.floor(seconds / 60);
		const secs = Math.floor(seconds % 60);
		return `${mins}:${secs.toString().padStart(2, '0')}`;
	}

	function handleSeek(e: Event) {
		const target = e.currentTarget as HTMLInputElement;
		if (audioElement) {
			audioElement.currentTime = parseFloat(target.value);
		}
	}

	$effect(() => {
		if (audioFilename && audioElement) {
			loadAudio();
		}
	});
</script>

<div class="audio-player" role="region" aria-label="Audio player">
	<audio
		bind:this={audioElement}
		ontimeupdate={handleTimeUpdate}
		onloadedmetadata={handleLoadedMetadata}
		onended={handleEnded}
		onplay={handlePlay}
		onpause={handlePause}
	></audio>

	{#if !audioFilename}
		<div class="upload-section">
			<label class="upload-label" for={`audio-upload-${bookId}`}>
				{#if isUploading}
					<span aria-live="polite">Uploading audio file... please wait</span>
				{:else}
					<span>Upload Audio</span>
				{/if}
			</label>
			<input
				id={`audio-upload-${bookId}`}
				type="file"
				accept="audio/*"
				onchange={handleFileUpload}
				disabled={isUploading}
				aria-label="Upload audio file"
				hidden
			/>
			{#if errorMessage}
				<span class="error-message" role="alert" aria-live="assertive">{errorMessage}</span>
			{/if}
		</div>
	{:else}
		<div class="player-controls">
			<button
				class="play-btn"
				onclick={togglePlay}
				aria-label={isPlaying ? 'Pause' : 'Play'}
				tabindex="0"
				type="button"
			>
				{isPlaying ? '⏸' : '▶'}
			</button>

			<div class="progress-section">
				<span class="time" aria-label="Current time">{formatTime(currentTime)}</span>
				<input
					type="range"
					min="0"
					max={duration || 100}
					value={currentTime}
					oninput={handleSeek}
					class="progress-bar"
					aria-label="Audio progress"
					aria-valuemin={0}
					aria-valuemax={duration || 100}
					aria-valuenow={currentTime}
				/>
				<span class="time" aria-label="Total duration">{formatTime(duration)}</span>
			</div>
		</div>
	{/if}
</div>

<style>
	.audio-player {
		display: flex;
		align-items: center;
		padding: 0.75rem;
		background: var(--theme-surface);
		border-radius: 0.5rem;
		gap: 1rem;
	}
	.upload-section {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
	}
	.error-message {
		color: var(--theme-error);
		font-size: 0.75rem;
		margin-top: 0.25rem;
	}
	.upload-label {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		padding: 0.5rem 1rem;
		background: var(--theme-primary);
		color: var(--theme-on-primary);
		border-radius: 0.375rem;
		cursor: pointer;
		font-size: 0.875rem;
	}
	.upload-label:hover {
		opacity: 0.9;
	}
	.player-controls {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		flex: 1;
	}
	.play-btn {
		width: 2.5rem;
		height: 2.5rem;
		border-radius: 50%;
		border: none;
		background: var(--theme-primary);
		color: var(--theme-on-primary);
		cursor: pointer;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 1rem;
	}
	.play-btn:hover {
		opacity: 0.9;
	}
	.progress-section {
		display: flex;
		align-items: center;
		gap: 0.5rem;
		flex: 1;
	}
	.progress-bar {
		flex: 1;
		height: 0.375rem;
		-webkit-appearance: none;
		appearance: none;
		background: var(--theme-border);
		border-radius: 0.25rem;
		cursor: pointer;
	}
	.progress-bar::-webkit-slider-thumb {
		-webkit-appearance: none;
		appearance: none;
		width: 0.75rem;
		height: 0.75rem;
		border-radius: 50%;
		background: var(--theme-primary);
		cursor: pointer;
	}
	.time {
		font-size: 0.75rem;
		color: var(--theme-text-muted);
		min-width: 2.5rem;
	}
</style>
