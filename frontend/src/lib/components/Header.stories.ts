import Header from './Header.svelte';
import type { Meta, StoryObj } from '@storybook/sveltekit';

const meta: Meta<Header> = {
	title: 'Components/Header',
	component: Header,
	tags: ['autodocs'],
	argTypes: {
		title: {
			control: 'text',
			description: 'The title displayed in the header'
		}
	}
};

export default meta;
type Story = StoryObj<Header>;

export const Default: Story = {
	args: {
		title: 'Lightning Fluency'
	}
};

export const ShortTitle: Story = {
	args: {
		title: 'Home'
	}
};

export const LongTitle: Story = {
	args: {
		title: 'Language Learning Through Reading'
	}
};
