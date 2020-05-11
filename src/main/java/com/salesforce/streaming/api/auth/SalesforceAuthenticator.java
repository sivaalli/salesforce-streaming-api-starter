package com.salesforce.streaming.api.auth;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import com.salesforce.streaming.api.StreamingApiProperties;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SalesforceAuthenticator implements Authenticator, InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(SalesforceAuthenticator.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final StreamingApiProperties.SalesforceConfig config;
    private final HttpClient authJettyClient;

    public SalesforceAuthenticator(StreamingApiProperties.SalesforceConfig config) {
        this.config = config;

        authJettyClient = new HttpClient(new SslContextFactory.Client());
        authJettyClient.setConnectTimeout(TimeUnit.SECONDS.toMillis(5));
    }

    @Override
    public String authenticate() {
        logger.info("Authenticating with Salesforce");

        final Request authRequest = authJettyClient.newRequest(config.getBaseUrl())
                .method(HttpMethod.POST)
                .timeout(5000, TimeUnit.MILLISECONDS);

        authRequest.header(HttpHeader.CONTENT_TYPE, "application/json");
        authRequest.param("grant_type", "password");
        authRequest.param("client_id", config.getClientId());
        authRequest.param("username", config.getUsername());
        authRequest.param("client_secret", config.getClientSecret());
        authRequest.param("password", config.getPassword());

        final ContentResponse response;
        try {
            response = authRequest.send();
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new AuthenticationException("Authentication failed with Salesforce.", e);
        }
        if (response.getStatus() > 300) {
            throw new AuthenticationException("Authentication failed with status code " + response.getStatus());
        }

        final AuthResponseModel authResponseModel;
        try {
            authResponseModel = mapper.readValue(response.getContentAsString(), AuthResponseModel.class);
        } catch (JsonProcessingException e) {
            throw new AuthenticationException("De-serialization failed parsing authentication response", e);
        }
        logger.debug("Authentication Success. {}", authResponseModel);
        return authResponseModel.access_token;
    }

    @Override
    public void destroy() throws Exception {
        if (!authJettyClient.isStopped()) {
            authJettyClient.stop();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!authJettyClient.isStarted()) {
            try {
                authJettyClient.start();
            } catch (Exception e) {
                throw new AuthenticationException("Failed to start HttpClient to authenticate", e);
            }
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
    public static class AuthResponseModel {
        public String id;
        public String issued_at;
        public String instance_url;
        public String signature;
        public String access_token;
        public String token_type;

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("AuthResponse")
                    .add("id", id)
                    .add("issued_at", issued_at)
                    .add("instance_url", instance_url)
                    .add("signature", signature)
                    .add("access_token", "REDACTED")
                    .add("token_type", token_type)
                    .toString();
        }
    }
}
