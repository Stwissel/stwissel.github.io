/**
 * A/B test Worker: replicate Caddy's file_server semantics exactly.
 *
 * html_handling "none" disables every implicit mapping, which preserves the
 * site's .html URLs (no 308 to extensionless) but also removes directory ->
 * index.html resolution. This handler restores that one piece, so behaviour
 * matches Caddy: .html served directly, directories served from index.html.
 */

/**
 * Paths owned by the blog-comments Worker rather than the asset store.
 *
 * /why: the whole /.well-known/ prefix travels together. The comment Worker
 * serves webfinger plus the two static files the retired Java container held,
 * so narrowing this to /.well-known/webfinger would 404 the Azure
 * application-ownership proof.
 */
function isComments(pathname) {
  return (
    pathname === '/blogcomments' ||
    pathname.startsWith('/blogcomments/') ||
    pathname.startsWith('/.well-known/')
  );
}

/** Fetch one asset path from the assets binding. */
function asset(env, url, pathname, request) {
  return env.ASSETS.fetch(new Request(new URL(pathname + url.search, url.origin), request));
}

export default {
  async fetch(request, env) {
    const url = new URL(request.url);

    if (isComments(url.pathname)) {
      return env.COMMENTS.fetch(request);
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
