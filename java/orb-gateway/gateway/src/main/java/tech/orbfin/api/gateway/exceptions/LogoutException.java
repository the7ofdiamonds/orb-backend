package tech.orbfin.api.gateway.exceptions;

public class LogoutException extends RuntimeException {
    public LogoutException(String message) {
        super(message);
    }
}
