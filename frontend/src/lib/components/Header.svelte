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
	<h1 class="h1">{title}</h1>
	<div class="settings">
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
	}
	.settings {
		display: flex;
		gap: 1rem;
	}
</style>
