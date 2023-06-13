package net.arville.easybill.service.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.arville.easybill.model.User;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class AuthenticatedUserResult {
    private String accessToken;
    private User user;
}
