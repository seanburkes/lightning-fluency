import { get } from 'svelte/store';
import { describe, expect, it } from 'vitest';
import { settings } from './settings';

describe('settings store', () => {
	it('has default values', () => {
		const value = get(settings);
		expect(value.theme).toBe('cerberus');
		expect(value.fontSize).toBe(16);
		expect(value.showDefinitions).toBe(true);
		expect(value.autoPlayAudio).toBe(false);
	});

	it('setTheme updates theme', () => {
		settings.setTheme('rocket');
		expect(get(settings).theme).toBe('rocket');
	});

	it('setFontSize updates fontSize', () => {
		settings.setFontSize(20);
		expect(get(settings).fontSize).toBe(20);
	});

	it('toggleDefinitions toggles showDefinitions', () => {
		const initial = get(settings).showDefinitions;
		settings.toggleDefinitions();
		expect(get(settings).showDefinitions).toBe(!initial);
	});

	it('toggleAutoPlay toggles autoPlayAudio', () => {
		const initial = get(settings).autoPlayAudio;
		settings.toggleAutoPlay();
		expect(get(settings).autoPlayAudio).toBe(!initial);
	});

	it('reset restores default values', () => {
		settings.setTheme('rocket');
		settings.setFontSize(20);
		settings.reset();
		expect(get(settings).theme).toBe('cerberus');
		expect(get(settings).fontSize).toBe(16);
	});
});
