package tech.orbfin.api.gateway.exceptions;

public class UserCreationException extends RuntimeException {
    public UserCreationException() {
        super(ExceptionMessages.USER_CREATION_ERROR);
    }
}