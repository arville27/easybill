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
    private String newAccountNumber;

    @Override
    public Set<String> getMissingProperties() {
        Set<String> missingProperties = new LinkedHashSet<>();
        if (currentPassword == null || currentPassword.isBlank()) missingProperties.add("current_password");
        if (newAccountNumber == null || newAccountNumber.isBlank()) missingProperties.add("new_account_number");
        return missingProperties;
    }
}
