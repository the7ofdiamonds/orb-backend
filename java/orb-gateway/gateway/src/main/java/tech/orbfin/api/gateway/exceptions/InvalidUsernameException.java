package tech.orbfin.api.gateway.exceptions;

public class InvalidUsernameException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Username can not contain any special characters or spaces and a max length of 15 characters.";

    public InvalidUsernameException() {
        super(DEFAULT_MESSAGE);
    }
}
