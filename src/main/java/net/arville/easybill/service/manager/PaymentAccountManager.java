package net.arville.easybill.service.manager;

import net.arville.easybill.dto.request.UserChangeAccountNumberRequest;
import net.arville.easybill.model.PaymentAccount;
import net.arville.easybill.model.User;

public interface PaymentAccountManager {
    PaymentAccount deletePaymentAccount(Long paymentAccountId, User authenticatedUser);

    User savePaymentAccount(UserChangeAccountNumberRequest request, User authenticatedUser);
}
