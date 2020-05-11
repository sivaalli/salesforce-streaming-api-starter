package com.salesforce.streaming.api;

import com.salesforce.streaming.api.auth.AuthenticationManager;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Salesforce specific callback that adds authorization header to the request for client
 * authentication.
 */
public class SalesforceStreamingTransportCallback implements ClientTransportCallback {

    private static final Logger logger = LoggerFactory.getLogger(SalesforceStreamingTransportCallback.class);

    private final AuthenticationManager authMgr;

    public SalesforceStreamingTransportCallback(AuthenticationManager authMgr) {
        this.authMgr = authMgr;
    }

    @Override
    public void callback(Request request) {
        final String token = authMgr.getToken();
        logger.trace("Adding token {} to request header", token.substring(0, 5) + "_*");
        request.header(HttpHeader.AUTHORIZATION, token);
    }
}
