package net.arville.easybill.configuration;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import net.arville.easybill.constant.EasybillConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@AllArgsConstructor
public class BeanProvider {
    private final Environment env;

    @Bean
    public Algorithm tokenAlgorithm() {
        var secret = env.getProperty(EasybillConstants.TOKEN_SECRET);
        assert secret != null;
        return Algorithm.HMAC256(secret);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
