/**
 * Keyboard shortcuts store
 *
 * Manages keyboard event handling and shortcut registration.
 */
import { writable, type Writable } from 'svelte/store';

export interface KeyboardShortcut {
	key: string;
	description: string;
	action: () => void;
	ctrl?: boolean;
	shift?: boolean;
	alt?: boolean;
	preventDefault?: boolean;
}

interface KeyboardState {
	shortcuts: KeyboardShortcut[];
	helpOpen: boolean;
	enabled: boolean;
}

function createKeyboardStore() {
	const { subscribe, update }: Writable<KeyboardState> = writable({
		shortcuts: [],
		helpOpen: false,
		enabled: true
	});

	function registerShortcut(shortcut: KeyboardShortcut) {
		update((state) => ({
			...state,
			shortcuts: [...state.shortcuts, shortcut]
		}));
	}

	function unregisterShortcut(key: string) {
		update((state) => ({
			...state,
			shortcuts: state.shortcuts.filter((s) => s.key !== key)
		}));
	}

	function toggleHelp() {
		update((state) => ({
			...state,
			helpOpen: !state.helpOpen
		}));
	}

	function openHelp() {
		update((state) => ({
			...state,
			helpOpen: true
		}));
	}

	function closeHelp() {
		update((state) => ({
			...state,
			helpOpen: false
		}));
	}

	function setEnabled(enabled: boolean) {
		update((state) => ({
			...state,
			enabled
		}));
	}

	function handleKeydown(event: KeyboardEvent): boolean {
		let handled = false;

		subscribe((state) => {
			if (!state.enabled) return;

			// Don't handle if user is typing in an input
			const target = event.target as HTMLElement;
			const isInput =
				target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.isContentEditable;

			if (isInput && event.key !== 'Escape') return;

			for (const shortcut of state.shortcuts) {
				const keyMatch = event.key === shortcut.key;
				const ctrlMatch = shortcut.ctrl
					? event.ctrlKey || event.metaKey
					: !event.ctrlKey && !event.metaKey;
				const shiftMatch = shortcut.shift ? event.shiftKey : true;
				const altMatch = shortcut.alt ? event.altKey : !event.altKey;

				if (keyMatch && ctrlMatch && shiftMatch && altMatch) {
					if (shortcut.preventDefault !== false) {
						event.preventDefault();
					}
					shortcut.action();
					handled = true;
					break;
				}
			}
		})();

		return handled;
	}

	return {
		subscribe,
		registerShortcut,
		unregisterShortcut,
		toggleHelp,
		openHelp,
		closeHelp,
		setEnabled,
		handleKeydown
	};
}

export const keyboard = createKeyboardStore();
