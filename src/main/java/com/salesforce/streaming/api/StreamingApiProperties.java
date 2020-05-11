package com.salesforce.streaming.api;

import com.google.common.collect.ImmutableSet;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;
import java.util.concurrent.Executor;

@ConfigurationProperties("streaming.api")
public class StreamingApiProperties {

    private long handshakeTimeoutMs = 10000; // 5 secs
    private String baseUri;
    private long subscriptionTimeoutMs = 10000; // 10 secs
    private long shutdownTimeoutMs = 10000; // 10 secs
    private Transport transport;

    private SalesforceConfig creds;
    private Executor executor;

    public SalesforceConfig getCreds() {
        return creds;
    }

    public void setCreds(SalesforceConfig creds) {
        this.creds = creds;
    }

    public long getShutdownTimeoutMs() {
        return shutdownTimeoutMs;
    }

    public void setShutdownTimeoutMs(long shutdownTimeoutMs) {
        this.shutdownTimeoutMs = shutdownTimeoutMs;
    }

    public long getHandshakeTimeoutMs() {
        return handshakeTimeoutMs;
    }

    public void setHandshakeTimeoutMs(long handshakeTimeoutMs) {
        this.handshakeTimeoutMs = handshakeTimeoutMs;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public long getSubscriptionTimeoutMs() {
        return subscriptionTimeoutMs;
    }

    public void setSubscriptionTimeoutMs(long subscriptionTimeoutMs) {
        this.subscriptionTimeoutMs = subscriptionTimeoutMs;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    /* for now only long polling TODO: implement websocket */
    public enum Transport {WEBSOCKET, HTTP}

    public static class SalesforceConfig {
        private String clientId;
        private String clientSecret;
        private String baseUrl;
        private String username;
        private String password;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
