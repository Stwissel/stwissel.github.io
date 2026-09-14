import { proxyToOrigin } from '../../pages-lib/origin-proxy.js';

/** Handles /blogcomments/<anything>. */
export const onRequest = (context) => proxyToOrigin(context.request);
