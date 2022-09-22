package net.arville.easybill.service;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.*;
import net.arville.easybill.dto.request.UserRegistrationRequest;
import net.arville.easybill.dto.response.UserWithOrderResponse;
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

    public UserWithOrderResponse getUserRelevantOrder(Long userId) {
        var result = userRepository.findById(userId);
        if (result.isEmpty())
            throw new UserNotFoundException();

        var relevantOrderList = orderHeaderRepository
                .findRelevantOrderHeaderForUser(result.get().getId())
                .stream()
                .map(orderHeader -> OrderHeaderResponse.customMap(orderHeader, (entityBuilder, entity) ->
                        entityBuilder.id(entity.getId())
                                .userResponse(UserResponse.mapWithoutDate(entity.getUser()))
                                .upto(entity.getUpto())
                                .discount(entity.getDiscount())
                                .orderDescription(entity.getOrderDescription())
                                .totalPayment(entity.getTotalPayment())
                                .orderAt(entity.getOrderAt())
                                .createdAt(entity.getCreatedAt())
                                .updatedAt(entity.getUpdatedAt())
                                .build())
                )
                .collect(Collectors.toList());

        return UserWithOrderResponse.map(result.get(), relevantOrderList);
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
