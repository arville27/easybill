package net.arville.easybill.helper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.arville.easybill.model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@NoArgsConstructor
@Getter
@Setter
public class AuthenticatedUser {
    private User user;
    private Long userId;
    private String username;

    public User getUser() {
        if (this.user == null) {
            this.user = User.builder()
                    .username(username)
                    .id(userId)
                    .build();
        }
        return this.user;
    }
}