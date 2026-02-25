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

const defaultState: KeyboardState = {
	shortcuts: [],
	helpOpen: false,
	enabled: true
};

function createKeyboardStore() {
	let state = $state(defaultState);

	return {
		get shortcuts() {
			return state.shortcuts;
		},
		get helpOpen() {
			return state.helpOpen;
		},
		get enabled() {
			return state.enabled;
		},
		get state() {
			return state;
		},
		registerShortcut(shortcut: KeyboardShortcut) {
			state = { ...state, shortcuts: [...state.shortcuts, shortcut] };
		},
		unregisterShortcut(key: string) {
			state = { ...state, shortcuts: state.shortcuts.filter((s) => s.key !== key) };
		},
		toggleHelp() {
			state = { ...state, helpOpen: !state.helpOpen };
		},
		openHelp() {
			state = { ...state, helpOpen: true };
		},
		closeHelp() {
			state = { ...state, helpOpen: false };
		},
		setEnabled(enabled: boolean) {
			state = { ...state, enabled };
		},
		handleKeydown(event: KeyboardEvent): boolean {
			if (!state.enabled) return false;

			const target = event.target as HTMLElement;
			const isInput =
				target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.isContentEditable;

			if (isInput && event.key !== 'Escape') return false;

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
					return true;
				}
			}

			return false;
		}
	};
}

export const keyboard = createKeyboardStore();
