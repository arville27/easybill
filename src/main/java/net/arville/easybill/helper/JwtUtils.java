package net.arville.easybill.helper;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import net.arville.easybill.constant.EasybillConstants;
import net.arville.easybill.model.User;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtils {
    private final Environment env;
    private String secret;

    public Algorithm tokenAlgorithm() {
        if (secret == null)
            this.secret = env.getProperty(EasybillConstants.TOKEN_SECRET);
        assert secret != null;
        return Algorithm.HMAC256(secret);
    }

    public String createToken(User user) {
        return JWT.create()
                .withClaim("user_id", user.getId())
                .withSubject(user.getUsername())
                .withExpiresAt(EasybillConstants.JWT_EXPIRY_TIME)
                .sign(tokenAlgorithm());
    }

    public DecodedJWT verifyToken(String accessToken) {
        JWTVerifier jwtVerifier = JWT.require(tokenAlgorithm()).build();
        return jwtVerifier.verify(accessToken);
    }

}
