package tech.orbfin.api.gateway.exceptions;

public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }
}
