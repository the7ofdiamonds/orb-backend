package tech.orbfin.api.gateway.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super(ExceptionMessages.USER_NOT_FOUND);
    }
}
