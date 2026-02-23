import { describe, expect, it } from 'vitest';
import { render, screen } from '@testing-library/svelte';
import { axe } from 'jest-axe';
import Header from './Header.svelte';
import { get } from 'svelte/store';
import { settings } from '$lib/stores';

describe('Header component', () => {
	it('renders title prop', () => {
		render(Header, { title: 'Test Title' });
		expect(screen.getByText('Test Title')).toBeInTheDocument();
	});

	it('displays font size input with current settings value', () => {
		settings.setFontSize(18);
		render(Header, { title: 'Test' });
		const input = screen.getByRole('spinbutton');
		expect(input).toHaveValue(18);
	});

	it('updates settings when font size changes', async () => {
		render(Header, { title: 'Test' });
		const input = screen.getByRole('spinbutton') as HTMLInputElement;
		input.value = '20';
		input.dispatchEvent(new Event('input', { bubbles: true }));
		expect(get(settings).fontSize).toBe(20);
	});

	it('has no accessibility violations', async () => {
		const { container } = render(Header, { title: 'Test' });
		const results = await axe(container);
		expect(results).toHaveNoViolations();
	});
});
