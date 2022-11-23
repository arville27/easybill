package net.arville.easybill.service.implementation;

import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.UserChangeAccountNumberRequest;
import net.arville.easybill.dto.request.UserChangePasswordRequest;
import net.arville.easybill.dto.request.UserRegistrationRequest;
import net.arville.easybill.dto.response.OrderHeaderResponse;
import net.arville.easybill.dto.response.PaginationResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.exception.InvalidPropertiesValue;
import net.arville.easybill.exception.MissingRequiredPropertiesException;
import net.arville.easybill.exception.UserNotFoundException;
import net.arville.easybill.exception.UsernameAlreadyExists;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;
import net.arville.easybill.repository.OrderHeaderRepository;
import net.arville.easybill.repository.UserRepository;
import net.arville.easybill.repository.helper.PageableBuilder;
import net.arville.easybill.service.manager.UserManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserManagerImpl implements UserManager {
    private final UserRepository userRepository;
    private final OrderHeaderRepository orderHeaderRepository;
    private final PasswordEncoder encoder;
    private final PageableBuilder pageableBuilder = PageableBuilder.builder();

    public PaginationResponse<UserResponse> getUserRelevantOrder(User user, int pageNumber, int pageSize) {
        var relevantOrderList = orderHeaderRepository
                .findRelevantOrderHeaderForUser(
                        user.getId(),
                        pageableBuilder.setPageNumber(pageNumber).setPageSize(Math.min(pageSize, 25)).build()
                );

        var relevantOrderWithOthers = this.fetchRequiredOrderHeaderData(relevantOrderList.toList());

        var data = UserResponse
                .template(user)
                .orderHeaderResponseList(relevantOrderWithOthers
                        .map(order -> OrderHeaderResponse
                                .template(order)
                                .buyerResponse(UserResponse.mapWithoutDate(order.getBuyer()))
                                .relevantStatus(order.getRelevantStatus(user))
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .build();

        return PaginationResponse.<UserResponse>builder()
                .data(data)
                .page(relevantOrderList.getTotalPages() == 0 ? 0 : pageNumber)
                .pageSize(relevantOrderList.getNumberOfElements())
                .totalPages(relevantOrderList.getTotalPages())
                .totalItems(relevantOrderList.getTotalElements())
                .build();
    }

    public PaginationResponse<UserResponse> getUsersOrder(User user, int pageNumber, int pageSize) {
        var usersOrderList = orderHeaderRepository
                .findUsersOrderHeaderForUser(
                        user.getId(),
                        pageableBuilder.setPageNumber(pageNumber).setPageSize(Math.min(pageSize, 25)).build()
                );

        var relevantOrderWithOthers = this.fetchRequiredOrderHeaderData(usersOrderList.toList());

        var data = UserResponse
                .template(user)
                .orderHeaderResponseList(relevantOrderWithOthers
                        .map(order -> OrderHeaderResponse
                                .template(order)
                                .buyerResponse(UserResponse.mapWithoutDate(order.getBuyer()))
                                .relevantStatus(order.getRelevantStatusForUsersOrder())
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .build();

        return PaginationResponse.<UserResponse>builder()
                .data(data)
                .page(usersOrderList.getTotalPages() == 0 ? 0 : pageNumber)
                .pageSize(usersOrderList.getNumberOfElements())
                .totalPages(usersOrderList.getTotalPages())
                .totalItems(usersOrderList.getTotalElements())
                .build();
    }

    private Stream<OrderHeader> fetchRequiredOrderHeaderData(List<OrderHeader> orderHeaderList) {
        var listOrderHeaderId = orderHeaderList
                .stream()
                .map(OrderHeader::getId)
                .collect(Collectors.toSet());

        var relevantOrderDetail = orderHeaderRepository.findRelevantOrderDetail(listOrderHeaderId);
        var relevantBill = orderHeaderRepository.findRelevantBill(listOrderHeaderId);

        return orderHeaderList.stream()
                .peek(orderHeader -> orderHeader.setOrderDetailList(
                        relevantOrderDetail.stream()
                                .filter(orderDetail -> Objects.equals(orderDetail.getOrderHeader().getId(), orderHeader.getId()))
                                .collect(Collectors.toSet())
                ))
                .peek(orderHeader -> orderHeader.setBillList(
                        relevantBill.stream()
                                .filter(bill -> Objects.equals(bill.getOrderHeader().getId(), orderHeader.getId()))
                                .collect(Collectors.toSet())
                ));
    }

    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public void changeUserPassword(UserChangePasswordRequest request, User authenticatedUser) {
        var missingProperties = request.getMissingProperties();

        if (missingProperties.size() > 0)
            throw new MissingRequiredPropertiesException(missingProperties);

        var invalidPropertiesValue = new InvalidPropertiesValue();

        if (!Objects.equals(request.getNewPassword(), request.getConfirmPassword())) {
            invalidPropertiesValue.addInvalidProperty(
                    "confirm_password",
                    "new_password and confirm_password must be the same!"
            );
        }

        if (request.getNewPassword().length() < 8) {
            invalidPropertiesValue.addInvalidProperty(
                    "new_password",
                    "Minimum password length is 8 characters"
            );
        }

        if (invalidPropertiesValue.isThereInvalidProperties())
            throw invalidPropertiesValue;

        authenticatedUser = this.getUserByUserId(authenticatedUser.getId());
        if (!encoder.matches(request.getCurrentPassword(), authenticatedUser.getPassword())) {
            invalidPropertiesValue.addInvalidProperty(
                    "current_password",
                    "Current password is incorrect"
            );
            throw invalidPropertiesValue;
        }

        if (Objects.equals(request.getCurrentPassword(), request.getNewPassword())) {
            invalidPropertiesValue.addInvalidProperty(
                    "new_password",
                    "New password must be different with current password"
            );
            throw invalidPropertiesValue;
        }

        authenticatedUser.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(authenticatedUser);
    }

    @Override
    public void changeUserAccountNumber(UserChangeAccountNumberRequest request, User authenticatedUser) {
        var missingProperties = request.getMissingProperties();

        if (missingProperties.size() > 0)
            throw new MissingRequiredPropertiesException(missingProperties);

        var invalidPropertiesValue = new InvalidPropertiesValue();

        if (!request.getNewAccountNumber().matches("[0-9]+")) {
            invalidPropertiesValue.addInvalidProperty(
                    "new_account_number",
                    "Account number should only contains number"
            );
            throw invalidPropertiesValue;
        }

        authenticatedUser = this.getUserByUserId(authenticatedUser.getId());
        if (!encoder.matches(request.getCurrentPassword(), authenticatedUser.getPassword())) {
            invalidPropertiesValue.addInvalidProperty(
                    "current_password",
                    "Current password is incorrect"
            );
            throw invalidPropertiesValue;
        }

        if (!Objects.equals(authenticatedUser.getAccountNumber(), request.getNewAccountNumber())) {
            authenticatedUser.setAccountNumber(request.getNewAccountNumber());
            userRepository.save(authenticatedUser);
        }
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
