/**
 * A/B test Worker: replicate Caddy's file_server semantics exactly.
 *
 * html_handling "none" disables every implicit mapping, which preserves the
 * site's .html URLs (no 308 to extensionless) but also removes directory ->
 * index.html resolution. This handler restores that one piece, so behaviour
 * matches Caddy: .html served directly, directories served from index.html.
 */

/**
 * Hosts that must 301 to the canonical apex, and where they go.
 *
 * /why this lives in the Worker and not in _redirects: the asset store's
 * _redirects matches PATH patterns only. An absolute-URL source such as
 * "https://www.wissel.net/*" is accepted into the file and then silently
 * ignored -- verified live, the rule deployed and never fired while path rules
 * in the same file kept working. (Cloudflare Pages does support domain sources;
 * Workers static assets does not.) The Worker runs ahead of the asset store and
 * is the only layer that sees the Host header, so a host redirect belongs here.
 *
 * Deliberately NOT a generic "strip www": wissel.asia, notessensei.com,
 * wissel.sg and wissel.ph are bound to this same Worker and serve the blog on
 * purpose. Whether their www forms should collapse is a branding decision.
 */
const CANONICAL_HOSTS = new Map([['www.wissel.net', 'wissel.net']]);

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

    // Canonical host first: every later branch would serve a duplicate of the
    // whole site under the wrong hostname.
    const canonical = CANONICAL_HOSTS.get(url.hostname);
    if (canonical) {
      url.hostname = canonical;
      return Response.redirect(url.toString(), 301);
    }

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