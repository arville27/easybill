package net.arville.easybill.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.util.EnsureRequiredFields;

import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor
@Data
public class UserChangePasswordRequest implements EnsureRequiredFields {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    @Override
    public Set<String> getMissingProperties() {
        Set<String> missingProperties = new LinkedHashSet<>();
        if (currentPassword == null || currentPassword.isBlank()) missingProperties.add("current_password");
        if (newPassword == null || newPassword.isBlank()) missingProperties.add("new_password");
        if (confirmPassword == null || confirmPassword.isBlank()) missingProperties.add("confirm_password");
        return missingProperties;
    }
}
