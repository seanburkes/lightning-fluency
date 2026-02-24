import { writable, type Writable, type Updater } from 'svelte/store';

export function createStore<T>(initialValue: T) {
	const { subscribe, update, set }: Writable<T> = writable(initialValue);

	return {
		subscribe,
		set,
		update,
		reset: () => set(initialValue)
	};
}

export function createActionStore<T extends Record<string, unknown>>(
	initialValue: T,
	actions: (set: (value: T | Updater<T>) => void, get: () => T) => Record<string, unknown>
) {
	const store = createStore(initialValue);
	const boundActions = actions(store.set, store.subscribe as () => T);

	return {
		...store,
		...boundActions
	};
}
