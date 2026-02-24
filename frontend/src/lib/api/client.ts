export const API_BASE = '/api';

export interface HealthResponse {
	status: string;
}

export interface LanguageDto {
	id: number;
	name: string;
	parser_type: string;
	character_substitutions?: string;
	regexp_split_sentences?: string;
	exceptions_split_sentences?: string;
	regexp_word_characters?: string;
	right_to_left: boolean;
	show_romanization: boolean;
}

export interface CreateLanguageDto {
	name: string;
	parser_type: string;
	character_substitutions?: string;
	regexp_split_sentences?: string;
	exceptions_split_sentences?: string;
	regexp_word_characters?: string;
	right_to_left?: boolean;
	show_romanization?: boolean;
}

export interface UpdateLanguageDto {
	name?: string;
	parser_type?: string;
	character_substitutions?: string;
	regexp_split_sentences?: string;
	exceptions_split_sentences?: string;
	regexp_word_characters?: string;
	right_to_left?: boolean;
	show_romanization?: boolean;
}

export interface BookDto {
	id: number;
	title: string;
	language_id: number;
	language_name: string;
	source_uri?: string;
	archived: boolean;
	page_count: number;
	current_page: number;
	tags: string[];
	created_at?: string;
}

export interface CreateBookDto {
	title: string;
	language_id: number;
	content?: string;
	source_uri?: string;
}

export interface UpdateBookDto {
	title?: string;
	archived?: boolean;
}

export interface TermDto {
	id: number;
	text: string;
	language_id: number;
	status: number;
	translation?: string;
	romanization?: string;
	token_count: number;
	tags: string[];
	parents: string[];
	children_count: number;
	created_at?: string;
	status_changed_at?: string;
}

export interface CreateTermDto {
	text: string;
	language_id: number;
	translation?: string;
	romanization?: string;
	status?: number;
	tags?: number[];
}

export interface UpdateTermDto {
	text?: string;
	translation?: string;
	romanization?: string;
	status?: number;
}

async function handleResponse<T>(response: Response): Promise<T> {
	if (!response.ok) {
		const error = await response.json().catch(() => ({ error: 'Unknown error' }));
		throw new Error(error.error || error.message || `Request failed: ${response.status}`);
	}
	if (response.status === 204 || response.headers.get('content-length') === '0') {
		return undefined as T;
	}
	return response.json();
}

export const api = {
	health: {
		get: () => fetch(`${API_BASE}/health`).then(handleResponse<HealthResponse>)
	},
	languages: {
		getAll: (limit = 100, offset = 0) =>
			fetch(`${API_BASE}/languages?limit=${limit}&offset=${offset}`).then(
				handleResponse<LanguageDto[]>
			),
		getById: (id: number) => fetch(`${API_BASE}/languages/${id}`).then(handleResponse<LanguageDto>),
		create: (dto: CreateLanguageDto) =>
			fetch(`${API_BASE}/languages`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(dto)
			}).then(handleResponse<LanguageDto>),
		update: (id: number, dto: UpdateLanguageDto) =>
			fetch(`${API_BASE}/languages/${id}`, {
				method: 'PATCH',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(dto)
			}).then(handleResponse<LanguageDto>),
		delete: (id: number) =>
			fetch(`${API_BASE}/languages/${id}`, { method: 'DELETE' }).then(handleResponse<void>)
	},
	books: {
		getAll: (languageId?: number, archived?: boolean) => {
			const params = new URLSearchParams();
			if (languageId) params.set('language_id', String(languageId));
			if (archived !== undefined) params.set('archived', String(archived));
			const query = params.toString();
			return fetch(`${API_BASE}/books${query ? `?${query}` : ''}`).then(handleResponse<BookDto[]>);
		},
		getById: (id: number) => fetch(`${API_BASE}/books/${id}`).then(handleResponse<BookDto>),
		create: (dto: CreateBookDto) =>
			fetch(`${API_BASE}/books`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(dto)
			}).then(handleResponse<BookDto>),
		update: (id: number, dto: UpdateBookDto) =>
			fetch(`${API_BASE}/books/${id}`, {
				method: 'PATCH',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(dto)
			}).then(handleResponse<BookDto>),
		delete: (id: number) =>
			fetch(`${API_BASE}/books/${id}`, { method: 'DELETE' }).then(handleResponse<void>),
		getPage: (id: number, page: number) =>
			fetch(`${API_BASE}/books/${id}/pages/${page}`).then(handleResponse<unknown>)
	},
	terms: {
		getAll: (languageId?: number, status?: number, limit = 100, offset = 0) => {
			const params = new URLSearchParams({ limit: String(limit), offset: String(offset) });
			if (languageId) params.set('language_id', String(languageId));
			if (status !== undefined) params.set('status', String(status));
			return fetch(`${API_BASE}/terms?${params}`).then(handleResponse<TermDto[]>);
		},
		getById: (id: number) => fetch(`${API_BASE}/terms/${id}`).then(handleResponse<TermDto>),
		create: (dto: CreateTermDto) =>
			fetch(`${API_BASE}/terms`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(dto)
			}).then(handleResponse<TermDto>),
		update: (id: number, dto: UpdateTermDto) =>
			fetch(`${API_BASE}/terms/${id}`, {
				method: 'PATCH',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(dto)
			}).then(handleResponse<TermDto>),
		delete: (id: number) =>
			fetch(`${API_BASE}/terms/${id}`, { method: 'DELETE' }).then(handleResponse<void>),
		search: (query: string, languageId?: number, status?: number) => {
			const params = new URLSearchParams({ query });
			if (languageId) params.set('language_id', String(languageId));
			if (status !== undefined) params.set('status', String(status));
			return fetch(`${API_BASE}/terms/search?${params}`).then(handleResponse<TermDto[]>);
		}
	}
};

export const fetchHealth = api.health.get;
