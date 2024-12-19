import { test, expect } from '@playwright/test';

test.beforeEach(async ({ page }) => {
    await page.goto('https://todomvc.com/examples/vue/');
});

const ITEMS = [
    'Item1',
    'Item2',
    'Item3',
];

test.describe('Add Item', () => {
    test('should add a new item to the list', async ({ page }) => {
        const textInput = page.locator('.new-todo');

        // Add a new item
        await textInput.fill(ITEMS[0]);
        await textInput.press('Enter');

        // Verify item exists in the list
        await expect(page.locator('.view label')).toHaveText(ITEMS[0]);

        // Add another item
        await textInput.fill(ITEMS[1]);
        await textInput.press('Enter');

        // Verify two items exist
        await expect(page.locator('.view label')).toHaveText([
            ITEMS[0],
            ITEMS[1]
        ]);
    });
});

test.describe('Edit Item', () => {
    test('should edit an existing item in the list', async ({ page }) => {
        // Add sample items
        for (const item of ITEMS) {
            await page.locator('.new-todo').fill(item);
            await page.locator('.new-todo').press('Enter');
        }

        // Edit the content of the second item
        const items = page.locator('.todo-list li')
        const secondItem = items.nth(1)
        await secondItem.dblclick();
        const editBox = secondItem.locator('.edit')
        await expect(editBox).toHaveValue(ITEMS[1]);
        await editBox.fill('Item2_Updated');
        await editBox.press('Enter');

        // Verify item was updated
        await expect(items).toHaveText([ITEMS[0], 'Item2_Updated', ITEMS[2]]);
    });
});

test.describe('Item Counter', () => {
    test('should show the current list size', async ({ page }) => {
        const textInput = page.locator('.new-todo');
        const itemCounter = page.locator('.todo-count');

        // Add an item and check counter
        await textInput.fill(ITEMS[0]);
        await textInput.press('Enter');
        await expect(itemCounter).toContainText('1');

        // Add another item and recheck counter
        await textInput.fill(ITEMS[1]);
        await textInput.press('Enter');
        await expect(itemCounter).toContainText('2');

        // Mark the first item as completed and recheck counter
        page.locator('.todo-list li').nth(0).locator('.toggle').check();
        await expect(itemCounter).toContainText('1');
    });
});
