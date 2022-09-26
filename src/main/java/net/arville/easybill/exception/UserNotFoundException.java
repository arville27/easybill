package net.arville.easybill.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("User with id " + userId + " could not be found");
    }

    public UserNotFoundException(String username) {
        super("User with username " + username + " could not be found");
    }
}
