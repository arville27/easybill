package net.arville.easybill.helper;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import net.arville.easybill.constant.EasybillConstants;
import net.arville.easybill.model.User;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@AllArgsConstructor
public class JwtUtils {
    private static final Date EXPIRY_TIME = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
    private final Environment env;

    public Algorithm tokenAlgorithm() {
        var secret = env.getProperty(EasybillConstants.TOKEN_SECRET);
        assert secret != null;
        return Algorithm.HMAC256(secret);
    }

    public String createToken(User user) {
        return JWT.create()
                .withClaim("user_id", user.getId())
                .withSubject(user.getUsername())
                .withExpiresAt(EXPIRY_TIME)
                .sign(tokenAlgorithm());
    }

    public DecodedJWT verifyToken(String accessToken) {
        JWTVerifier jwtVerifier = JWT.require(tokenAlgorithm()).build();
        return jwtVerifier.verify(accessToken);
    }

}
