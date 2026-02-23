import { expect, test } from '@playwright/test';

test.describe('Home page', () => {
	test('has title', async ({ page }) => {
		await page.goto('/');
		await expect(page.locator('h1')).toBeVisible();
	});

	test('displays font size setting', async ({ page }) => {
		await page.goto('/');
		await expect(page.getByRole('spinbutton')).toBeVisible();
	});

	test('can change font size', async ({ page }) => {
		await page.goto('/');
		const input = page.getByRole('spinbutton');
		await input.fill('20');
		await expect(input).toHaveValue('20');
	});
});
