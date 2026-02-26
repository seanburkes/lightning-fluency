export interface Settings {
	theme: string;
	customCss: string;
	fontSize: number;
	showDefinitions: boolean;
	autoPlayAudio: boolean;
	backupEnabled: boolean;
	backupAuto: boolean;
	backupDir: string;
	backupCount: number;
	backupWarn: boolean;
	useAnkiConnect: boolean;
	ankiConnectUrl: string;
	showHighlights: boolean;
	focusMode: boolean;
	tapBehavior: string;
	defaultLanguageId: number | null;
}

export interface ThemeInfo {
	name: string;
	css: string;
}

const defaultSettings: Settings = {
	theme: 'cerberus',
	customCss: '',
	fontSize: 16,
	showDefinitions: true,
	autoPlayAudio: false,
	backupEnabled: true,
	backupAuto: false,
	backupDir: '',
	backupCount: 5,
	backupWarn: true,
	useAnkiConnect: false,
	ankiConnectUrl: 'http://localhost:8765',
	showHighlights: true,
	focusMode: false,
	tapBehavior: 'popup',
	defaultLanguageId: null
};

const STORAGE_KEY = 'lute-settings';

function createSettings() {
	let theme = $state(defaultSettings.theme);
	let customCss = $state(defaultSettings.customCss);
	let fontSize = $state(defaultSettings.fontSize);
	let showDefinitions = $state(defaultSettings.showDefinitions);
	let autoPlayAudio = $state(defaultSettings.autoPlayAudio);
	let backupEnabled = $state(defaultSettings.backupEnabled);
	let backupAuto = $state(defaultSettings.backupAuto);
	let backupDir = $state(defaultSettings.backupDir);
	let backupCount = $state(defaultSettings.backupCount);
	let backupWarn = $state(defaultSettings.backupWarn);
	let useAnkiConnect = $state(defaultSettings.useAnkiConnect);
	let ankiConnectUrl = $state(defaultSettings.ankiConnectUrl);
	let showHighlights = $state(defaultSettings.showHighlights);
	let focusMode = $state(defaultSettings.focusMode);
	let tapBehavior = $state(defaultSettings.tapBehavior);
	let defaultLanguageId = $state<number | null>(defaultSettings.defaultLanguageId);

	function loadFromStorage(): Partial<Settings> {
		if (typeof window === 'undefined') return {};
		try {
			const stored = localStorage.getItem(STORAGE_KEY);
			if (stored) return JSON.parse(stored);
		} catch {
			// ignore
		}
		return {};
	}

	function saveToStorage() {
		if (typeof window === 'undefined') return;
		try {
			localStorage.setItem(
				STORAGE_KEY,
				JSON.stringify({
					theme,
					customCss,
					fontSize,
					showDefinitions,
					autoPlayAudio,
					backupEnabled,
					backupAuto,
					backupDir,
					backupCount,
					backupWarn,
					useAnkiConnect,
					ankiConnectUrl,
					showHighlights,
					focusMode,
					tapBehavior,
					defaultLanguageId
				})
			);
		} catch {
			// ignore
		}
	}

	function initialize() {
		const stored = loadFromStorage();
		if (stored.theme) theme = stored.theme;
		if (stored.customCss !== undefined) customCss = stored.customCss;
		if (stored.fontSize) fontSize = stored.fontSize;
		if (stored.showDefinitions !== undefined) showDefinitions = stored.showDefinitions;
		if (stored.autoPlayAudio !== undefined) autoPlayAudio = stored.autoPlayAudio;
		if (stored.backupEnabled !== undefined) backupEnabled = stored.backupEnabled;
		if (stored.backupAuto !== undefined) backupAuto = stored.backupAuto;
		if (stored.backupDir !== undefined) backupDir = stored.backupDir;
		if (stored.backupCount !== undefined) backupCount = stored.backupCount;
		if (stored.backupWarn !== undefined) backupWarn = stored.backupWarn;
		if (stored.useAnkiConnect !== undefined) useAnkiConnect = stored.useAnkiConnect;
		if (stored.ankiConnectUrl !== undefined) ankiConnectUrl = stored.ankiConnectUrl;
		if (stored.showHighlights !== undefined) showHighlights = stored.showHighlights;
		if (stored.focusMode !== undefined) focusMode = stored.focusMode;
		if (stored.tapBehavior !== undefined) tapBehavior = stored.tapBehavior;
		if (stored.defaultLanguageId !== undefined) defaultLanguageId = stored.defaultLanguageId;
	}

	return {
		get theme() {
			return theme;
		},
		get customCss() {
			return customCss;
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
		get backupEnabled() {
			return backupEnabled;
		},
		get backupAuto() {
			return backupAuto;
		},
		get backupDir() {
			return backupDir;
		},
		get backupCount() {
			return backupCount;
		},
		get backupWarn() {
			return backupWarn;
		},
		get useAnkiConnect() {
			return useAnkiConnect;
		},
		get ankiConnectUrl() {
			return ankiConnectUrl;
		},
		get showHighlights() {
			return showHighlights;
		},
		get focusMode() {
			return focusMode;
		},
		get tapBehavior() {
			return tapBehavior;
		},
		get defaultLanguageId() {
			return defaultLanguageId;
		},
		setTheme(value: string) {
			theme = value;
			saveToStorage();
		},
		setCustomCss(value: string) {
			customCss = value;
			saveToStorage();
		},
		setFontSize(value: number) {
			fontSize = value;
			saveToStorage();
		},
		toggleDefinitions() {
			showDefinitions = !showDefinitions;
			saveToStorage();
		},
		toggleAutoPlay() {
			autoPlayAudio = !autoPlayAudio;
			saveToStorage();
		},
		setBackupEnabled(value: boolean) {
			backupEnabled = value;
			saveToStorage();
		},
		setBackupAuto(value: boolean) {
			backupAuto = value;
			saveToStorage();
		},
		setBackupDir(value: string) {
			backupDir = value;
			saveToStorage();
		},
		setBackupCount(value: number) {
			backupCount = value;
			saveToStorage();
		},
		setBackupWarn(value: boolean) {
			backupWarn = value;
			saveToStorage();
		},
		setUseAnkiConnect(value: boolean) {
			useAnkiConnect = value;
			saveToStorage();
		},
		setAnkiConnectUrl(value: string) {
			ankiConnectUrl = value;
			saveToStorage();
		},
		setShowHighlights(value: boolean) {
			showHighlights = value;
			saveToStorage();
		},
		setFocusMode(value: boolean) {
			focusMode = value;
			saveToStorage();
		},
		setTapBehavior(value: string) {
			tapBehavior = value;
			saveToStorage();
		},
		setDefaultLanguageId(value: number | null) {
			defaultLanguageId = value;
			saveToStorage();
		},
		reset() {
			theme = defaultSettings.theme;
			customCss = defaultSettings.customCss;
			fontSize = defaultSettings.fontSize;
			showDefinitions = defaultSettings.showDefinitions;
			autoPlayAudio = defaultSettings.autoPlayAudio;
			backupEnabled = defaultSettings.backupEnabled;
			backupAuto = defaultSettings.backupAuto;
			backupDir = defaultSettings.backupDir;
			backupCount = defaultSettings.backupCount;
			backupWarn = defaultSettings.backupWarn;
			useAnkiConnect = defaultSettings.useAnkiConnect;
			ankiConnectUrl = defaultSettings.ankiConnectUrl;
			showHighlights = defaultSettings.showHighlights;
			focusMode = defaultSettings.focusMode;
			tapBehavior = defaultSettings.tapBehavior;
			defaultLanguageId = defaultSettings.defaultLanguageId;
			saveToStorage();
		},
		initialize,
		get state() {
			return {
				theme,
				customCss,
				fontSize,
				showDefinitions,
				autoPlayAudio,
				backupEnabled,
				backupAuto,
				backupDir,
				backupCount,
				backupWarn,
				useAnkiConnect,
				ankiConnectUrl,
				showHighlights,
				focusMode,
				tapBehavior,
				defaultLanguageId
			};
		}
	};
}

export const settings = createSettings();
