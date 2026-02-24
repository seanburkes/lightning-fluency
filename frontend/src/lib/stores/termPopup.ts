/**
 * Term popup store for managing term definitions
 */
import { writable, type Writable } from 'svelte/store';

export interface TermPopupState {
	isOpen: boolean;
	term: string;
	definition: string;
	wordId: number | null;
	x: number;
	y: number;
}

const defaultState: TermPopupState = {
	isOpen: false,
	term: '',
	definition: '',
	wordId: null,
	x: 0,
	y: 0
};

function createTermPopupStore() {
	const { subscribe, update, set }: Writable<TermPopupState> = writable(defaultState);

	function open(term: string, definition: string, wordId: number, x: number, y: number) {
		set({
			isOpen: true,
			term,
			definition,
			wordId,
			x,
			y
		});
	}

	function close() {
		set(defaultState);
	}

	function toggle() {
		update((state) => ({ ...state, isOpen: !state.isOpen }));
	}

	return {
		subscribe,
		open,
		close,
		toggle
	};
}

export const termPopup = createTermPopupStore();
