package net.arville.easybill.service.implementation;

import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.UserRegistrationRequest;
import net.arville.easybill.dto.response.OrderHeaderResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.exception.MissingRequiredPropertiesException;
import net.arville.easybill.exception.UserNotFoundException;
import net.arville.easybill.exception.UsernameAlreadyExists;
import net.arville.easybill.model.User;
import net.arville.easybill.repository.OrderHeaderRepository;
import net.arville.easybill.repository.UserRepository;
import net.arville.easybill.service.manager.UserManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagerImpl implements UserManager {
    private final UserRepository userRepository;
    private final OrderHeaderRepository orderHeaderRepository;
    private final PasswordEncoder encoder;

    public UserResponse getUserRelevantOrder(User user) {
        var relevantOrderList = orderHeaderRepository
                .findRelevantOrderHeaderForUser(user.getId());

        return UserResponse
                .template(user)
                .orderHeaderResponseList(relevantOrderList
                        .stream()
                        .map(order -> OrderHeaderResponse
                                .template(order)
                                .buyerResponse(UserResponse.mapWithoutDate(order.getBuyer()))
                                .relevantStatus(order.getRelevantStatus(user))
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .build();
    }

    public UserResponse getUsersOrder(User user) {
        var usersOrderList = orderHeaderRepository
                .findUsersOrderHeaderForUser(user.getId());

        return UserResponse
                .template(user)
                .orderHeaderResponseList(usersOrderList
                        .stream()
                        .map(order -> OrderHeaderResponse
                                .template(order)
                                .buyerResponse(UserResponse.mapWithoutDate(order.getBuyer()))
                                .relevantStatus(order.getRelevantStatusForUsersOrder())
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .build();
    }

    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    public UserResponse addNewUser(UserRegistrationRequest request) {

        var missingProperties = request.getMissingProperties();

        if (missingProperties.size() > 0) {
            throw new MissingRequiredPropertiesException(missingProperties);
        }

        if (userRepository.findUserByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExists();
        }

        User newUser = request.toOriginalEntity();
        newUser.setPassword(encoder.encode(newUser.getPassword()));

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
