package tech.orbfin.api.gateway.exceptions;

public class InvalidPasswordException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "A password is required";

    public InvalidPasswordException() {
        super(DEFAULT_MESSAGE);
    }
}