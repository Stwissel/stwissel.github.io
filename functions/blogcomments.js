import { proxyToOrigin } from '../pages-lib/origin-proxy.js';

/** Handles the bare /blogcomments path, which the comment widget POSTs to. */
export const onRequest = (context) => proxyToOrigin(context.request);
