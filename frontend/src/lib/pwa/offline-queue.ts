import type { CreateTermDto, UpdateTermDto } from '$lib/api';

const DB_NAME = 'lightning-fluency';
const DB_VERSION = 1;
const STORE_NAME = 'offline_queue';

export type QueueEntry =
	| { type: 'term_create'; data: CreateTermDto }
	| { type: 'term_update'; data: { id: number } & UpdateTermDto };

export type QueueItem = QueueEntry & {
	id?: number;
	timestamp: Date;
	retries: number;
};

function openDB(): Promise<IDBDatabase> {
	return new Promise((resolve, reject) => {
		const request = indexedDB.open(DB_NAME, DB_VERSION);

		request.onupgradeneeded = () => {
			const db = request.result;
			if (!db.objectStoreNames.contains(STORE_NAME)) {
				db.createObjectStore(STORE_NAME, {
					keyPath: 'id',
					autoIncrement: true
				});
			}
		};

		request.onsuccess = () => resolve(request.result);
		request.onerror = () => reject(request.error);
	});
}

async function withDB<T>(
	mode: IDBTransactionMode,
	fn: (store: IDBObjectStore) => IDBRequest
): Promise<T> {
	const db = await openDB();
	try {
		return await new Promise<T>((resolve, reject) => {
			const tx = db.transaction(STORE_NAME, mode);
			const store = tx.objectStore(STORE_NAME);
			const request = fn(store);
			request.onsuccess = () => resolve(request.result as T);
			tx.onerror = () => reject(tx.error);
			tx.oncomplete = () => db.close();
			tx.onabort = () => db.close();
		});
	} catch (error) {
		db.close();
		throw error;
	}
}

export async function addToQueue(item: Omit<QueueItem, 'id'>): Promise<number> {
	return withDB<number>('readwrite', (store) => store.add(item));
}

export async function getQueue(): Promise<QueueItem[]> {
	return withDB<QueueItem[]>('readonly', (store) => store.getAll());
}

export async function removeFromQueue(id: number): Promise<void> {
	return withDB<void>('readwrite', (store) => store.delete(id));
}

export async function clearQueue(): Promise<void> {
	return withDB<void>('readwrite', (store) => store.clear());
}

export async function getQueueCount(): Promise<number> {
	return withDB<number>('readonly', (store) => store.count());
}

export async function updateRetries(id: number, retries: number): Promise<void> {
	const db = await openDB();
	try {
		await new Promise<void>((resolve, reject) => {
			const tx = db.transaction(STORE_NAME, 'readwrite');
			const store = tx.objectStore(STORE_NAME);
			const getRequest = store.get(id);
			getRequest.onsuccess = () => {
				const item = getRequest.result as QueueItem | undefined;
				if (item) {
					item.retries = retries;
					store.put(item);
				}
				resolve();
			};
			tx.onerror = () => reject(tx.error);
			tx.oncomplete = () => db.close();
			tx.onabort = () => db.close();
		});
	} catch (error) {
		db.close();
		throw error;
	}
}
