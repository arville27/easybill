package net.arville.easybill.service.implementation;

import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.UserChangeAccountNumberRequest;
import net.arville.easybill.exception.InvalidPropertiesValue;
import net.arville.easybill.exception.MissingRequiredPropertiesException;
import net.arville.easybill.exception.PaymentAccountAlreadyExists;
import net.arville.easybill.exception.PaymentAccountNotFoundException;
import net.arville.easybill.model.PaymentAccount;
import net.arville.easybill.model.User;
import net.arville.easybill.repository.PaymentAccountRepository;
import net.arville.easybill.repository.UserRepository;
import net.arville.easybill.service.manager.PaymentAccountManager;
import net.arville.easybill.service.manager.UserManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentAccountService implements PaymentAccountManager {
    private final UserRepository userRepository;
    private final PaymentAccountRepository paymentAccountRepository;
    private final UserManager userManager;
    private final PasswordEncoder encoder;

    @Override
    public PaymentAccount deletePaymentAccount(Long paymentAccountId, User authenticatedUser) {
        authenticatedUser = userManager.getUserByUserId(authenticatedUser.getId());
        var userPaymentAccountList = authenticatedUser.getPaymentAccountList();
        var deletedPaymentAccount = userPaymentAccountList.stream()
                .filter(paymentAccount -> paymentAccount.getId().equals(paymentAccountId))
                .findFirst()
                .orElseThrow(() -> new PaymentAccountNotFoundException(paymentAccountId));
        userPaymentAccountList.remove(deletedPaymentAccount);

        userRepository.save(authenticatedUser);
        paymentAccountRepository.delete(deletedPaymentAccount);

        return deletedPaymentAccount;
    }

    @Override
    public User savePaymentAccount(UserChangeAccountNumberRequest request, User authenticatedUser) {
        var missingProperties = request.getMissingProperties();

        if (missingProperties.size() > 0)
            throw new MissingRequiredPropertiesException(missingProperties);

        var invalidPropertiesValue = new InvalidPropertiesValue();

        if (request.getPaymentAccount().length() > 15) {
            invalidPropertiesValue.addInvalidProperty(
                    "payment_account",
                    "Payment account length should be between 5 and 15 characters"
            );
            throw invalidPropertiesValue;
        }

        if (request.getPaymentAccountLabel().length() > 15
                || request.getPaymentAccountLabel().length() < 1
        ) {
            invalidPropertiesValue.addInvalidProperty(
                    "payment_account_label",
                    "Payment account label length should be between 1 and 15 characters"
            );
            throw invalidPropertiesValue;
        }

        authenticatedUser = userManager.getUserByUserId(authenticatedUser.getId());
        if (!encoder.matches(request.getCurrentPassword(), authenticatedUser.getPassword())) {
            invalidPropertiesValue.addInvalidProperty(
                    "current_password",
                    "Current password is incorrect"
            );
            throw invalidPropertiesValue;
        }

        var userPaymentAccountList = authenticatedUser.getPaymentAccountList();

        var isNewPaymentAccount = Objects.isNull(request.getId());
        if (isNewPaymentAccount && userPaymentAccountList.size() == 3) {
            invalidPropertiesValue.addInvalidProperty(
                    "new_account_number",
                    "Only 3 payment account can be added"
            );
            throw invalidPropertiesValue;
        }

        var isDuplicatePaymentAccount = userPaymentAccountList.stream().anyMatch(paymentAccount ->
            Objects.equals(paymentAccount.getPaymentAccount(), request.getPaymentAccount())
                && Objects.equals(paymentAccount.getPaymentAccountLabel(), request.getPaymentAccountLabel())
        );

        if (isDuplicatePaymentAccount)
            throw new PaymentAccountAlreadyExists(
                    request.getPaymentAccount(),
                    request.getPaymentAccountLabel()
            );

       if (!isNewPaymentAccount) {
            userPaymentAccountList.stream()
                    .filter(paymentAccount -> Objects.equals(
                            paymentAccount.getId(),
                            request.getId())
                    )
                    .findAny()
                    .ifPresentOrElse(
                            paymentAccount -> {
                                paymentAccount.setPaymentAccount(request.getPaymentAccount());
                                paymentAccount.setPaymentAccountLabel(request.getPaymentAccountLabel());
                            },
                            () -> {
                                throw new PaymentAccountNotFoundException(request.getId());
                            }
                    );
        } else {
            var paymentAccountData = PaymentAccount.builder()
                    .user(authenticatedUser)
                    .paymentAccountLabel(request.getPaymentAccountLabel())
                    .paymentAccount(request.getPaymentAccount())
                    .build();
            userPaymentAccountList.add(paymentAccountData);
        }

        return userRepository.save(authenticatedUser);
    }
}
