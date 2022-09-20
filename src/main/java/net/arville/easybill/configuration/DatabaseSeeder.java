package net.arville.easybill.configuration;

import net.arville.easybill.model.User;
import net.arville.easybill.repository.OrderHeaderRepository;
import net.arville.easybill.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DatabaseSeeder {

    @Bean
    public CommandLineRunner runner(
            UserRepository userRepository,
            OrderHeaderRepository orderHeaderRepository
    ){

        return args -> {

            User yohanes = new User();
            yohanes.setUsername("yohanes");
            yohanes.setPassword("yohanesjelek");

            User arville = new User();
            arville.setUsername("arville27");
            arville.setPassword("arvillejelek");

            User jossen = new User();
            jossen.setUsername("jossen");
            jossen.setPassword("jossenjelek");

            userRepository.saveAll(List.of(yohanes, arville, jossen));
        };

    }

}
