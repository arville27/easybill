package net.arville.easybill.service.implementation;

import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.UserChangePasswordRequest;
import net.arville.easybill.dto.request.UserChangeUsernameRequest;
import net.arville.easybill.dto.request.UserRegistrationRequest;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.exception.InvalidPropertiesValue;
import net.arville.easybill.exception.MissingRequiredPropertiesException;
import net.arville.easybill.exception.UserNotFoundException;
import net.arville.easybill.exception.UsernameAlreadyExists;
import net.arville.easybill.model.User;
import net.arville.easybill.repository.PaymentAccountRepository;
import net.arville.easybill.repository.UserRepository;
import net.arville.easybill.service.manager.UserManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagerImpl implements UserManager {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public User getUserByUsername(String username) {
        return userRepository.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundException(username));
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
    public void changeUserUsername(UserChangeUsernameRequest request, User authenticatedUser) {
        var missingProperties = request.getMissingProperties();

        if (missingProperties.size() > 0)
            throw new MissingRequiredPropertiesException(missingProperties);

        var invalidPropertiesValue = new InvalidPropertiesValue();

        if (request.getNewUsername().length() < 3 || request.getNewUsername().length() > 10) {
            invalidPropertiesValue.addInvalidProperty(
                    "new_username",
                    "Username should only consist of 3 to 10 characters"
            );
            throw invalidPropertiesValue;
        }

        if (request.getNewUsername().equalsIgnoreCase(authenticatedUser.getUsername())) {
            invalidPropertiesValue.addInvalidProperty(
                    "new_username",
                    "New username should be different from the current username"
            );
            throw invalidPropertiesValue;
        }

        var userWithNewUsername = userRepository
                .findUserByUsernameIgnoreCase(request.getNewUsername());

        if (userWithNewUsername.isPresent()) throw new UsernameAlreadyExists(request.getNewUsername());

        authenticatedUser = this.getUserByUserId(authenticatedUser.getId());
        if (!encoder.matches(request.getCurrentPassword(), authenticatedUser.getPassword())) {
            invalidPropertiesValue.addInvalidProperty(
                    "current_password",
                    "Current password is incorrect"
            );
            throw invalidPropertiesValue;
        }

        authenticatedUser.setUsername(request.getNewUsername().toLowerCase());
        userRepository.save(authenticatedUser);
    }

    public UserResponse addNewUser(UserRegistrationRequest request) {

        var missingProperties = request.getMissingProperties();

        if (missingProperties.size() > 0) {
            throw new MissingRequiredPropertiesException(missingProperties);
        }

        var invalidPropertiesValue = new InvalidPropertiesValue();

        if (request.getUsername().length() < 3 || request.getUsername().length() > 10) {
            invalidPropertiesValue.addInvalidProperty(
                    "username",
                    "Username should only consist of 3 to 10 characters"
            );
            throw invalidPropertiesValue;
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
