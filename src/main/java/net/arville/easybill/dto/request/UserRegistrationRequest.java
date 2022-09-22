package net.arville.easybill.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.util.ConvertibleToOriginalEntity;
import net.arville.easybill.dto.util.EnsureRequiredFields;
import net.arville.easybill.model.User;

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
    public boolean isAllPresent() {
        return this.username != null && this.password != null;
    }
}
