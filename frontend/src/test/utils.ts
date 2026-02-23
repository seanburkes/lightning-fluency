import { render } from '@testing-library/svelte';
import type { Component, ComponentProps } from 'svelte';

export function renderComponent<T extends Component>(
	component: T,
	options?: Omit<ComponentProps<T>, 'children'>
) {
	return render(component, { props: options });
}

export { screen, waitFor, fireEvent } from '@testing-library/svelte';
export { default as userEvent } from '@testing-library/user-event';
