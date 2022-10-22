package net.arville.easybill.configuration;

import net.arville.easybill.model.User;
import net.arville.easybill.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DatabaseSeeder {

    @Bean
    public CommandLineRunner runner(
            UserRepository userRepository,
            PasswordEncoder encoder
    ) {

        return args -> {

            User yohanes = new User();
            yohanes.setUsername("yohanes");
            yohanes.setPassword(encoder.encode("yohanesjelek"));

            User arville = new User();
            arville.setUsername("arville27");
            arville.setPassword(encoder.encode("arvillejelek"));

            User jossen = new User();
            jossen.setUsername("jossen");
            jossen.setPassword(encoder.encode("jossenjelek"));

            User winata = new User();
            winata.setUsername("winata");
            winata.setPassword(encoder.encode("winatajelek"));

            userRepository.saveAll(List.of(yohanes, arville, jossen, winata));
        };

    }

}
