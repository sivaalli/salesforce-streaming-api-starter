package com.salesforce.streaming.api;

public class CometdBootstrapException extends RuntimeException {
    private static final long serialVersionUID = 3986256962209418528L;

    public CometdBootstrapException(String message, Throwable cause) {
        super(message, cause);
    }

    public CometdBootstrapException(String message) {
        super(message);
    }

    public CometdBootstrapException(Throwable cause) {
        super(cause);
    }
}
