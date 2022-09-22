package net.arville.easybill.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.util.ConvertibleToOriginalEntity;
import net.arville.easybill.dto.util.EnsureRequiredFields;
import net.arville.easybill.model.User;

import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor
@Data
public class UserRegistrationRequest implements EnsureRequiredFields, ConvertibleToOriginalEntity<User> {

    private String username;

    private String password;

    @Override
    public User toOriginalEntity() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    @Override
    public Set<String> getMissingProperties() {
        Set<String> missingProperties = new LinkedHashSet<>();
        if (username == null) missingProperties.add("username");
        if (password == null) missingProperties.add("password");
        return missingProperties;
    }
}
