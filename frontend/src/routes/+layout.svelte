<script lang="ts">
	import '../app.css';
	import { keyboard, navigation, termPopup } from '$lib/stores';
	import { HelpModal } from '$lib/components';

	let { children } = $props();

	const shortcuts = [
		{ key: 'ArrowRight', description: 'Next page' },
		{ key: 'ArrowLeft', description: 'Previous page' },
		{ key: ' ', description: 'Next page (space)' },
		{ key: 'Enter', description: 'Open term popup' },
		{ key: 'Escape', description: 'Close term popup / Close modal' },
		{ key: '?', description: 'Show this help' }
	];

	function handleGlobalKeydown(event: KeyboardEvent) {
		keyboard.handleKeydown(event);
	}

	keyboard.registerShortcut({
		key: '?',
		description: 'Show keyboard shortcuts help',
		action: () => keyboard.toggleHelp()
	});

	keyboard.registerShortcut({
		key: 'ArrowRight',
		description: 'Next page',
		action: () => navigation.nextPage()
	});

	keyboard.registerShortcut({
		key: 'ArrowLeft',
		description: 'Previous page',
		action: () => navigation.prevPage()
	});

	keyboard.registerShortcut({
		key: ' ',
		description: 'Next page',
		action: () => navigation.nextPage(),
		preventDefault: false
	});

	keyboard.registerShortcut({
		key: 'Escape',
		description: 'Close modal / Clear selection',
		action: () => {
			keyboard.closeHelp();
			termPopup.close();
		}
	});

	keyboard.registerShortcut({
		key: 'Enter',
		description: 'Open term popup',
		action: () => {
			// Will be connected to reading view when implemented
		}
	});
</script>

<svelte:window onkeydown={handleGlobalKeydown} />

<svelte:head>
	<link rel="icon" href="/favicon.svg" />
</svelte:head>

{@render children()}
<HelpModal {shortcuts} />
