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
	let state = $state(defaultState);

	return {
		get isOpen() {
			return state.isOpen;
		},
		get term() {
			return state.term;
		},
		get definition() {
			return state.definition;
		},
		get wordId() {
			return state.wordId;
		},
		get x() {
			return state.x;
		},
		get y() {
			return state.y;
		},
		get state() {
			return state;
		},
		open(term: string, definition: string, wordId: number, x: number, y: number) {
			state = { isOpen: true, term, definition, wordId, x, y };
		},
		close() {
			state = { ...defaultState };
		},
		toggle() {
			state = { ...state, isOpen: !state.isOpen };
		}
	};
}

export const termPopup = createTermPopupStore();
