package net.arville.easybill.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.arville.easybill.dto.util.EnsureRequiredFields;

import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@Data
public class UserChangeUsernameRequest implements EnsureRequiredFields {
    private String currentPassword;
    private String newUsername;

    @Override
    public Set<String> getMissingProperties() {
        Set<String> missingProperties = new LinkedHashSet<>();
        if (currentPassword == null || currentPassword.isBlank()) missingProperties.add("current_password");
        if (newUsername == null || newUsername.isBlank()) missingProperties.add("new_username");
        return missingProperties;
    }
}
