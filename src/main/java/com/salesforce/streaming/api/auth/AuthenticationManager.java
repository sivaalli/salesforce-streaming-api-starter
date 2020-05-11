package com.salesforce.streaming.api.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles caching auth tokens and re-authenticating.
 */
public class AuthenticationManager {

    private static final String SALESFORCE_TOKEN = "salesforce_token";
    private final Authenticator authenticator;
    private final Map<String, String> token = new ConcurrentHashMap<>(1);

    public AuthenticationManager(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public String getToken() {
        return token.computeIfAbsent(SALESFORCE_TOKEN, key -> authenticator.authenticate());
    }

    public void reAuthenticate() {
        token.compute(SALESFORCE_TOKEN, (key, oldValue) -> authenticator.authenticate());
    }
}
