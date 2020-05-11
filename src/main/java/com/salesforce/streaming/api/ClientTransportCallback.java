package com.salesforce.streaming.api;

import org.eclipse.jetty.client.api.Request;

public interface ClientTransportCallback {

    /**
     * A final chance to customize the request. Add any headers that are required.
     *
     * @param request http request to be customized
     */
    void callback(Request request);
}
