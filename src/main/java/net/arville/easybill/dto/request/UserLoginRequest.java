package net.arville.easybill.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.util.EnsureRequiredFields;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserLoginRequest implements EnsureRequiredFields {
    private String username;
    private String password;

    @Override
    public Set<String> getMissingProperties() {
        Set<String> missingProperties = new LinkedHashSet<>();
        if (username == null) missingProperties.add("username");
        if (password == null) missingProperties.add("password");
        return missingProperties;
    }
}
