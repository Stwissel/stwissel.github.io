/**
 * A/B test Worker: replicate Caddy's file_server semantics exactly.
 *
 * html_handling "none" disables every implicit mapping, which preserves the
 * site's .html URLs (no 308 to extensionless) but also removes directory ->
 * index.html resolution. This handler restores that one piece, so behaviour
 * matches Caddy: .html served directly, directories served from index.html.
 */

const ORIGIN = 'https://origin.wissel.net';

/** Paths that must reach the comments container rather than static assets. */
function isProxied(pathname) {
  return (
    pathname === '/blogcomments' ||
    pathname.startsWith('/blogcomments/') ||
    pathname.startsWith('/.well-known/')
  );
}

/** Forward a request to the Caddy origin, preserving method, body and query. */
function proxy(request, url) {
  const target = new URL(url.pathname + url.search, ORIGIN);
  const headers = new Headers(request.headers);
  const clientIp = request.headers.get('CF-Connecting-IP');
  if (clientIp) {
    headers.set('X-Real-IP', clientIp);
  }
  headers.delete('Host');

  const init = { method: request.method, headers, redirect: 'manual' };
  if (request.method !== 'GET' && request.method !== 'HEAD') {
    init.body = request.body;
  }
  return fetch(new Request(target, init));
}

/** Fetch one asset path from the assets binding. */
function asset(env, url, pathname, request) {
  return env.ASSETS.fetch(new Request(new URL(pathname + url.search, url.origin), request));
}

export default {
  async fetch(request, env) {
    const url = new URL(request.url);

    if (isProxied(url.pathname)) {
      return proxy(request, url);
    }

    // Directory URLs: "/blog/" -> "/blog/index.html"
    if (url.pathname.endsWith('/')) {
      return asset(env, url, url.pathname + 'index.html', request);
    }

    const direct = await asset(env, url, url.pathname, request);
    if (direct.status !== 404) {
      return direct;
    }

    // Extensionless path that is really a directory: "/blog" -> "/blog/index.html".
    // Only attempted when the direct lookup missed, so real 404s stay 404s.
    if (!url.pathname.split('/').pop().includes('.')) {
      const nested = await asset(env, url, url.pathname + '/index.html', request);
      if (nested.status !== 404) {
        return nested;
      }
    }
    return direct;
  }
};
