package com.khov.sampleRegistration.exception;

/**
 *
 * @author t-less
 */
public class EmailExistsException extends RuntimeException {

    public EmailExistsException() {
        super();
    }

    public EmailExistsException(final String message) {
        super(message);
    }

}
