package com.sforce.sforcetrading.exception;

/**
 * Custom exception class for handling OAuth service-related errors.
 */
public class OAuthServiceException extends RuntimeException {

    /**
     * Constructs a new OAuthServiceException with the specified detail message.
     *
     * @param message the detail message.
     */
    public OAuthServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new OAuthServiceException with the specified detail message and
     * cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception.
     */
    public OAuthServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
