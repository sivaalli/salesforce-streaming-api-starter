package com.salesforce.streaming.api.auth;

public interface Authenticator {

    /**
     * Authenticates and returns and authentication token.
     *
     * @return access token.
     * @throws AuthenticationException if authentication fails.
     */
    String authenticate();
}
