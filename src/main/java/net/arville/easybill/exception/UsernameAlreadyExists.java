package net.arville.easybill.exception;

public class UsernameAlreadyExists extends RuntimeException {
    public UsernameAlreadyExists(String username) {
        super("User with username " + username + " already exists");
    }
}
