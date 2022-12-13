package net.arville.easybill.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UnauthorizedRequestException extends RuntimeException {
    public UnauthorizedRequestException(String message) {
        super(message);
    }
}
