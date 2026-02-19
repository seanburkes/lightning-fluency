import { writable } from 'svelte/store';

export interface Settings {
	theme: string;
	fontSize: number;
	showDefinitions: boolean;
	autoPlayAudio: boolean;
}

const defaultSettings: Settings = {
	theme: 'cerberus',
	fontSize: 16,
	showDefinitions: true,
	autoPlayAudio: false
};

function createSettingsStore() {
	const { subscribe, set, update } = writable<Settings>(defaultSettings);

	return {
		subscribe,
		set,
		update,
		reset: () => set(defaultSettings),
		setTheme: (theme: string) => update((s) => ({ ...s, theme })),
		setFontSize: (fontSize: number) => update((s) => ({ ...s, fontSize })),
		toggleDefinitions: () => update((s) => ({ ...s, showDefinitions: !s.showDefinitions })),
		toggleAutoPlay: () => update((s) => ({ ...s, autoPlayAudio: !s.autoPlayAudio }))
	};
}

export const settings = createSettingsStore();
