import { api, type CreateTermDto, type TermDto, type UpdateTermDto } from '$lib/api';
import { addToQueue } from './offline-queue';
import { syncStatus } from '$lib/stores';

export async function createTermOffline(dto: CreateTermDto): Promise<TermDto | null> {
	try {
		return await api.terms.create(dto);
	} catch (error) {
		if (!navigator.onLine) {
			await addToQueue({
				type: 'term_create',
				data: dto,
				timestamp: new Date(),
				retries: 0
			});
			await syncStatus.refreshCount();
			return null;
		}
		throw error;
	}
}

export async function updateTermOffline(id: number, dto: UpdateTermDto): Promise<TermDto | null> {
	try {
		return await api.terms.update(id, dto);
	} catch (error) {
		if (!navigator.onLine) {
			await addToQueue({
				type: 'term_update',
				data: { id, ...dto },
				timestamp: new Date(),
				retries: 0
			});
			await syncStatus.refreshCount();
			return null;
		}
		throw error;
	}
}
