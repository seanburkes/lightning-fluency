import { api } from '$lib/api';
import {
	getQueue,
	removeFromQueue,
	updateRetries,
	type QueueEntry,
	type QueueItem
} from './offline-queue';

const MAX_RETRIES = 3;

export interface SyncResult {
	synced: number;
	failed: number;
	retrying: number;
	remaining: number;
}

async function processItem(item: QueueItem): Promise<boolean> {
	try {
		switch (item.type) {
			case 'term_create':
				await api.terms.create(item.data);
				return true;
			case 'term_update': {
				const { id, ...dto } = item.data;
				await api.terms.update(id, dto);
				return true;
			}
			default: {
				const exhaustiveCheck: never = item;
				console.warn(`Unknown queue item type: ${(exhaustiveCheck as QueueEntry).type}`);
				return false;
			}
		}
	} catch (error) {
		console.error(`Failed to process queue item ${item.id}:`, error);
		return false;
	}
}

export async function syncQueue(): Promise<SyncResult> {
	const items = await getQueue();
	let synced = 0;
	let failed = 0;
	let retrying = 0;

	for (const item of items) {
		if (!navigator.onLine) break;

		const success = await processItem(item);
		if (success) {
			await removeFromQueue(item.id!);
			synced++;
		} else {
			const newRetries = item.retries + 1;
			if (newRetries >= MAX_RETRIES) {
				await removeFromQueue(item.id!);
				failed++;
				console.error(`Queue item ${item.id} failed after ${MAX_RETRIES} retries, removing`);
			} else {
				await updateRetries(item.id!, newRetries);
				retrying++;
			}
		}
	}

	const remaining = (await getQueue()).length;
	return { synced, failed, retrying, remaining };
}
