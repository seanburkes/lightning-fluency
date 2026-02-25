interface NavigationState {
	currentPage: number;
	totalPages: number;
	canGoNext: boolean;
	canGoPrev: boolean;
}

const defaultState: NavigationState = {
	currentPage: 1,
	totalPages: 1,
	canGoNext: false,
	canGoPrev: false
};

function createNavigationStore() {
	let state = $state(defaultState);

	return {
		get currentPage() {
			return state.currentPage;
		},
		get totalPages() {
			return state.totalPages;
		},
		get canGoNext() {
			return state.canGoNext;
		},
		get canGoPrev() {
			return state.canGoPrev;
		},
		get state() {
			return state;
		},
		setPage(page: number) {
			const newPage = Math.max(1, Math.min(page, state.totalPages));
			state = {
				...state,
				currentPage: newPage,
				canGoPrev: newPage > 1,
				canGoNext: newPage < state.totalPages
			};
		},
		nextPage() {
			if (!state.canGoNext) return;
			const newPage = state.currentPage + 1;
			state = {
				...state,
				currentPage: newPage,
				canGoNext: newPage < state.totalPages,
				canGoPrev: true
			};
		},
		prevPage() {
			if (!state.canGoPrev) return;
			const newPage = state.currentPage - 1;
			state = {
				...state,
				currentPage: newPage,
				canGoPrev: newPage > 1,
				canGoNext: true
			};
		},
		setTotalPages(total: number) {
			state = {
				...state,
				totalPages: total,
				canGoNext: total > 1 && state.currentPage < total,
				canGoPrev: state.currentPage > 1
			};
		},
		reset() {
			state = { currentPage: 1, totalPages: 1, canGoNext: false, canGoPrev: false };
		}
	};
}

export const navigation = createNavigationStore();
