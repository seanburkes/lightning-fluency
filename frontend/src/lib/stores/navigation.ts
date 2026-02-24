/**
 * Navigation store for page navigation
 */
import { writable, type Writable } from 'svelte/store';

interface NavigationState {
	currentPage: number;
	totalPages: number;
	canGoNext: boolean;
	canGoPrev: boolean;
}

function createNavigationStore() {
	const { subscribe, update, set }: Writable<NavigationState> = writable({
		currentPage: 1,
		totalPages: 1,
		canGoNext: false,
		canGoPrev: false
	});

	function setPage(page: number) {
		update((state) => {
			const newPage = Math.max(1, Math.min(page, state.totalPages));
			return {
				...state,
				currentPage: newPage,
				canGoPrev: newPage > 1,
				canGoNext: newPage < state.totalPages
			};
		});
	}

	function nextPage() {
		update((state) => {
			if (!state.canGoNext) return state;
			const newPage = state.currentPage + 1;
			return {
				...state,
				currentPage: newPage,
				canGoNext: newPage < state.totalPages,
				canGoPrev: true
			};
		});
	}

	function prevPage() {
		update((state) => {
			if (!state.canGoPrev) return state;
			const newPage = state.currentPage - 1;
			return {
				...state,
				currentPage: newPage,
				canGoPrev: newPage > 1,
				canGoNext: true
			};
		});
	}

	function setTotalPages(total: number) {
		update((state) => ({
			...state,
			totalPages: total,
			canGoNext: total > 1 && state.currentPage < total,
			canGoPrev: state.currentPage > 1
		}));
	}

	return {
		subscribe,
		setPage,
		nextPage,
		prevPage,
		setTotalPages,
		reset: () => set({ currentPage: 1, totalPages: 1, canGoNext: false, canGoPrev: false })
	};
}

export const navigation = createNavigationStore();
