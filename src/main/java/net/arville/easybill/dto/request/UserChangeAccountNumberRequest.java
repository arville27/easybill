package net.arville.easybill.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.util.EnsureRequiredFields;

import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor
@Data
public class UserChangeAccountNumberRequest implements EnsureRequiredFields {
    private String currentPassword;
    private Long id;
    private String paymentAccountLabel;
    private String paymentAccount;

    @Override
    public Set<String> getMissingProperties() {
        Set<String> missingProperties = new LinkedHashSet<>();
        if (currentPassword == null || currentPassword.isBlank()) missingProperties.add("current_password");
        if (paymentAccountLabel == null || paymentAccountLabel.isEmpty())
            missingProperties.add("payment_account_label");
        if (paymentAccount == null || paymentAccount.isEmpty()) missingProperties.add("payment_account");
        return missingProperties;
    }
}
