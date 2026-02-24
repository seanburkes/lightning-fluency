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

function createSettings() {
	let theme = $state(defaultSettings.theme);
	let fontSize = $state(defaultSettings.fontSize);
	let showDefinitions = $state(defaultSettings.showDefinitions);
	let autoPlayAudio = $state(defaultSettings.autoPlayAudio);

	return {
		get theme() {
			return theme;
		},
		get fontSize() {
			return fontSize;
		},
		get showDefinitions() {
			return showDefinitions;
		},
		get autoPlayAudio() {
			return autoPlayAudio;
		},
		setTheme(value: string) {
			theme = value;
		},
		setFontSize(value: number) {
			fontSize = value;
		},
		toggleDefinitions() {
			showDefinitions = !showDefinitions;
		},
		toggleAutoPlay() {
			autoPlayAudio = !autoPlayAudio;
		},
		reset() {
			theme = defaultSettings.theme;
			fontSize = defaultSettings.fontSize;
			showDefinitions = defaultSettings.showDefinitions;
			autoPlayAudio = defaultSettings.autoPlayAudio;
		},
		get state() {
			return {
				theme,
				fontSize,
				showDefinitions,
				autoPlayAudio
			};
		}
	};
}

export const settings = createSettings();
