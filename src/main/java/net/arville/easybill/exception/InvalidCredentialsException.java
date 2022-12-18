package net.arville.easybill.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Failed login attempt, invalid credentials");
    }
}
