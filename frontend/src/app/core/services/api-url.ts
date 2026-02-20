import { environment } from '../../../environments/environment';

/** Runtime API base URL (set from backend /config when loaded); fallback is build-time environment.apiUrl. */
let runtimeApiUrl: string | null = null;

export function setRuntimeApiUrl(url: string | null): void {
  runtimeApiUrl = url;
}

/**
 * API base URL with no trailing slash (avoids double-slash when appending paths).
 * Uses runtime config from backend when available, else environment.apiUrl.
 */
export function getApiBase(): string {
  const base = (runtimeApiUrl ?? environment.apiUrl ?? '').replace(/\/+$/, '');
  return base;
}

/**
 * Builds a full API URL from a path with no double slashes.
 * Use this for all API requests so base + path never produces "//".
 */
export function getApiUrl(path: string): string {
  const base = getApiBase();
  const normalizedPath = (path || '').replace(/^\//, '');
  return base + (normalizedPath ? '/' + normalizedPath : '');
}

/**
 * Loads public config from backend (GET {base}/config) and sets runtime API URL.
 * Call at app bootstrap; uses environment.apiUrl for the initial request.
 */
export function loadRuntimeConfig(): Promise<void> {
  const base = (environment.apiUrl ?? '').replace(/\/+$/, '');
  if (!base) return Promise.resolve();
  return fetch(base + '/config')
    .then(r => (r.ok ? r.json() : {}))
    .then((data: { apiUrl?: string }) => {
      if (data.apiUrl && typeof data.apiUrl === 'string') {
        setRuntimeApiUrl(data.apiUrl.replace(/\/+$/, ''));
      }
    })
    .catch(() => {});
}
