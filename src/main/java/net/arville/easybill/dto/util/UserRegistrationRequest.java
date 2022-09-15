package net.arville.easybill.dto.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.model.User;

@NoArgsConstructor
@Data
public class UserRegistrationRequest implements EnsureRequiredFields, ConvertableToOriginalEntity<User> {

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
    public boolean isAllPresent() {
        return this.username != null && this.password != null;
    }
}
