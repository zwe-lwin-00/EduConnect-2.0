import { environment } from '../../../environments/environment';

/**
 * API base URL with no trailing slash (avoids double-slash when appending paths).
 */
export function getApiBase(): string {
  return (environment.apiUrl || '').replace(/\/+$/, '');
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
