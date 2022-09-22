package net.arville.easybill.service;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.request.UserRegistrationRequest;
import net.arville.easybill.dto.response.OrderHeaderResponse;
import net.arville.easybill.dto.response.UserResponse;
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

    public UserResponse getUserRelevantOrder(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        var relevantOrderList = orderHeaderRepository
                .findRelevantOrderHeaderForUser(user.getId());

        UserResponse userResponse = UserResponse.map(user);
        userResponse.setOrderHeaderResponseList(
                relevantOrderList.stream()
                        .map(OrderHeaderResponse::map)
                        .peek(orderHeaderResponse -> orderHeaderResponse.setOrderDetailResponses(null))
                        .collect(Collectors.toList())
        );
        return userResponse;
    }

    public UserResponse addNewUser(UserRegistrationRequest request) {

        if (!request.isAllPresent()) {
            throw new MissingRequiredPropertiesException();
        }

        if (userRepository.findUserByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExists();
        }

        User newUser = request.toOriginalEntity();
//        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        userRepository.save(newUser);

        return UserResponse.map(newUser);
    }

    public List<UserResponse> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::map)
                .collect(Collectors.toList());
    }
}
