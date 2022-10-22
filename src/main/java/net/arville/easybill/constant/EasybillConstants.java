package net.arville.easybill.constant;

import java.util.Date;
import java.util.List;

public final class EasybillConstants {
    public static final String TOKEN_SECRET = "easybill.token.secret";
    public static final String AUTH_PATH = "/api/auth";
    public static final Date JWT_EXPIRY_TIME = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); // 30 Days
    public static final List<String> ALLOWED_ORIGINS = List.of(
            "http://10.20.158.12:4200",
            "http://192.168.100.96:4200",
            "http://localhost:4200"
    );

    public static final List<String> UNAUTHENTICATED_ROUTES_PREFIX = List.of(AUTH_PATH, "/api/docs/**", "/swagger-ui/**");

}
