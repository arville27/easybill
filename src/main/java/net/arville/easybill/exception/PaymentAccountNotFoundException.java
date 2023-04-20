package net.arville.easybill.exception;

public class PaymentAccountNotFoundException extends RuntimeException {
    public PaymentAccountNotFoundException(Object paymentAccountId) {
        super("Payment account with id " + paymentAccountId + " could not be found for this user");
    }
}
