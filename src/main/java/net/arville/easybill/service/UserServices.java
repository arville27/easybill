package net.arville.easybill.service;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.*;
import net.arville.easybill.exception.UserNotFoundException;
import net.arville.easybill.exception.UsernameAlreadyExists;
import net.arville.easybill.model.User;
import net.arville.easybill.repository.OrderHeaderRepository;
import net.arville.easybill.repository.UserRepository;
import org.springframework.core.env.MissingRequiredPropertiesException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServices {

    private UserRepository userRepository;
    private OrderHeaderRepository orderHeaderRepository;

    public UserResponse getUser(Long userId) {
        var result = userRepository.findById(userId);
        if (result.isEmpty())
            throw new UserNotFoundException();
        return (new UserResponse()).fromOriginalEntity(result.get());
    }

    public UserWithOrderResponse getUserRelevantOrder(Long userId) {
        var result = userRepository.findById(userId);
        if (result.isEmpty())
            throw new UserNotFoundException();

        var relevantOrderList = orderHeaderRepository
                .findRelevantOrderHeaderForUser(result.get().getId())
                .stream().map(orderHeader -> (new OrderHeaderWithoutDetailOrder()).fromOriginalEntity(orderHeader))
                .collect(Collectors.toList());
        
        var userWithOrder = (new UserWithOrderResponse()).fromOriginalEntity(result.get(), relevantOrderList);

        return userWithOrder;
    }

    public UserRegistrationResponse addNewUser(UserRegistrationRequest request) {

        if (!request.isAllPresent()) {
            throw new MissingRequiredPropertiesException();
        }

        if (userRepository.findUserByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExists();
        }

        User newUser = request.toOriginalEntity();
//        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        userRepository.save(newUser);

        return new UserRegistrationResponse().fromOriginalEntity(newUser);
    }

    public List<UserResponse> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(user -> (new UserResponse()).fromOriginalEntity(user))
                .collect(Collectors.toList());
    }
}
