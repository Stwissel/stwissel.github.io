import { proxyToOrigin } from '../../pages-lib/origin-proxy.js';

/** Handles /.well-known/<anything> — ActivityPub WebFinger lives here. */
export const onRequest = (context) => proxyToOrigin(context.request);
