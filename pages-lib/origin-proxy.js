/**
 * Shared origin proxy for Cloudflare Pages Functions.
 *
 * Forwards a request to the Caddy origin on the droplet, preserving method,
 * body, query string and headers. Caddy rewrites Host to wissel.net on its
 * side, so canonical URLs in ActivityPub/WebFinger payloads stay correct.
 */

const ORIGIN = 'https://origin.wissel.net';

/**
 * @param {Request} request - the inbound Pages request
 * @returns {Promise<Response>} the origin's response, streamed back unchanged
 */
export async function proxyToOrigin(request) {
  const incoming = new URL(request.url);
  const target = new URL(incoming.pathname + incoming.search, ORIGIN);

  const headers = new Headers(request.headers);
  // Caddy's config expects X-Real-IP; Cloudflare supplies CF-Connecting-IP.
  const clientIp = request.headers.get('CF-Connecting-IP');
  if (clientIp) {
    headers.set('X-Real-IP', clientIp);
  }
  // Host is a forbidden header here; Caddy sets it via header_up instead.
  headers.delete('Host');

  const init = {
    method: request.method,
    headers,
    redirect: 'manual'
  };
  if (request.method !== 'GET' && request.method !== 'HEAD') {
    init.body = request.body;
  }

  return fetch(new Request(target, init));
}
