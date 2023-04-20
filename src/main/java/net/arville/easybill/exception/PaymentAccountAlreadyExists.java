package net.arville.easybill.exception;

public class PaymentAccountAlreadyExists extends RuntimeException{
    public PaymentAccountAlreadyExists(String paymentAccount, String paymentAccountLabel) {
        super("Payment account with label " + paymentAccountLabel +" and payment account " + paymentAccount + " is already exists");
    }
}
