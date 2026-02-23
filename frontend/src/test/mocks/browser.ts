import { setupWorker } from 'msw/browser';
import { handlers } from './handlers';

export const worker = setupWorker(...handlers);

export async function startMsw() {
	return worker.start({ onUnhandledRequest: 'bypass' });
}
