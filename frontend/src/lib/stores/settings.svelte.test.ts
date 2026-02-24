import { describe, expect, it } from 'vitest';
import { settings } from './settings.svelte';

describe('settings store', () => {
	it('has default values', () => {
		settings.reset();
		expect(settings.theme).toBe('cerberus');
		expect(settings.fontSize).toBe(16);
		expect(settings.showDefinitions).toBe(true);
		expect(settings.autoPlayAudio).toBe(false);
	});

	it('setTheme updates theme', () => {
		settings.setTheme('rocket');
		expect(settings.theme).toBe('rocket');
	});

	it('setFontSize updates fontSize', () => {
		settings.setFontSize(20);
		expect(settings.fontSize).toBe(20);
	});

	it('toggleDefinitions toggles showDefinitions', () => {
		const initial = settings.showDefinitions;
		settings.toggleDefinitions();
		expect(settings.showDefinitions).toBe(!initial);
	});

	it('toggleAutoPlay toggles autoPlayAudio', () => {
		const initial = settings.autoPlayAudio;
		settings.toggleAutoPlay();
		expect(settings.autoPlayAudio).toBe(!initial);
	});

	it('reset restores default values', () => {
		settings.setTheme('rocket');
		settings.setFontSize(20);
		settings.reset();
		expect(settings.theme).toBe('cerberus');
		expect(settings.fontSize).toBe(16);
	});
});
