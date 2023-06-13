package net.arville.easybill.constant;

import java.util.List;

public final class EasybillConstants {
    public static final String TOKEN_SECRET = "easybill.token-secret";
    public static final String AUTH_PATH = "/api/auth";
    public static final Long JWT_EXPIRY_TIME = 3 * 24 * 60 * 60 * 1000L; // 3 Days
    public static final List<String> ALLOWED_ORIGINS = List.of(
            "http://localhost:4200",
            "https://easybill.arville.net",
            "https://easybill-beta.arville.net",
            "http://10.10.10.2:4200"
    );

    public static final List<String> UNAUTHENTICATED_ROUTES_PREFIX = List.of(AUTH_PATH, "/api/docs/**", "/swagger-ui/**");

}
