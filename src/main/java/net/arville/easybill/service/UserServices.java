package net.arville.easybill.service;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.UserRegistrationRequest;
import net.arville.easybill.exception.UserNotFoundException;
import net.arville.easybill.model.User;
import net.arville.easybill.repository.UserRepository;
import org.springframework.core.env.MissingRequiredPropertiesException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServices {

    UserRepository userRepository;

    public User getUser(Long userId) {
        var result = userRepository.findById(userId);
        if (result.isEmpty())
            throw new UserNotFoundException();
        return result.get();
    }

    public User addNewUser(UserRegistrationRequest request) {

        if (request.isAllPresent()) {
            throw new MissingRequiredPropertiesException();
        }

        User newUser = request.toOriginalEntity();
//        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        return userRepository.save(newUser);
    }
}
