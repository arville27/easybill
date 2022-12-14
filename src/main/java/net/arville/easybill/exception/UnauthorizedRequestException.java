package net.arville.easybill.exception;

import lombok.Getter;

@Getter
public class UnauthorizedRequestException extends RuntimeException {

    private final boolean forceLogoutUser;

    public UnauthorizedRequestException(String message, boolean forceLogoutUser) {
        super(message);
        this.forceLogoutUser = forceLogoutUser;
    }

    public UnauthorizedRequestException(boolean forceLogoutUser) {
        this.forceLogoutUser = forceLogoutUser;
    }
}
