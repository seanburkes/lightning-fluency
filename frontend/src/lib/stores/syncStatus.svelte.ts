import { getQueueCount, syncQueue, type SyncResult } from '$lib/pwa';

export interface SyncState {
	pendingCount: number;
	syncing: boolean;
	lastResult: SyncResult | null;
	online: boolean;
}

const defaultState: SyncState = {
	pendingCount: 0,
	syncing: false,
	lastResult: null,
	online: typeof navigator !== 'undefined' ? navigator.onLine : true
};

function createSyncStore() {
	let state = $state(defaultState);

	async function refreshCount() {
		try {
			state = { ...state, pendingCount: await getQueueCount() };
		} catch {
			// IndexedDB may not be available in SSR
		}
	}

	async function sync() {
		if (state.syncing || !state.online) return;
		state = { ...state, syncing: true };
		try {
			const lastResult = await syncQueue();
			state = { ...state, lastResult };
			await refreshCount();
		} finally {
			state = { ...state, syncing: false };
		}
	}

	function setOnline(value: boolean) {
		state = { ...state, online: value };
		if (value) {
			sync();
		}
	}

	return {
		get pendingCount() {
			return state.pendingCount;
		},
		get syncing() {
			return state.syncing;
		},
		get lastResult() {
			return state.lastResult;
		},
		get online() {
			return state.online;
		},
		get state() {
			return state;
		},
		refreshCount,
		sync,
		setOnline
	};
}

export const syncStatus = createSyncStore();
