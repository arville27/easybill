package net.arville.easybill.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.UserChangePasswordRequest;
import net.arville.easybill.dto.request.UserChangeUsernameRequest;
import net.arville.easybill.dto.request.UserRegistrationRequest;
import net.arville.easybill.dto.response.PaymentAccountResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.helper.AuthenticatedUser;
import net.arville.easybill.payload.core.ResponseStatus;
import net.arville.easybill.payload.core.ResponseStructure;
import net.arville.easybill.service.manager.UserManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "User", description = "User resource")
@SecurityRequirement(name = "Access Token")
@RequiredArgsConstructor
public class UserController {
    private final UserManager userManager;
    private final AuthenticatedUser authenticatedUser;

    @GetMapping
    public ResponseEntity<ResponseStructure> getAllUser() {

        var users = userManager.getAllUser();
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(users);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseStructure> getUserData() {

        var user = userManager.getUserByUserId(authenticatedUser.getUserId());
        var userResponse = UserResponse.template(user)
                .paymentAccountList(user.getPaymentAccountList()
                        .stream()
                        .map(PaymentAccountResponse::map)
                        .toList()
                )
                .build();
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(userResponse);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping
    public ResponseEntity<ResponseStructure> addNewUser(
            @RequestBody UserRegistrationRequest request
    ) {

        var newUser = userManager.addNewUser(request);
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PutMapping("/username")
    public ResponseEntity<ResponseStructure> changeUserUsername(
            @RequestBody UserChangeUsernameRequest request
    ) {

        userManager.changeUserUsername(request, authenticatedUser.getUser());
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(null);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PutMapping("/password")
    public ResponseEntity<ResponseStructure> changeUserPassword(
            @RequestBody UserChangePasswordRequest request
    ) {

        userManager.changeUserPassword(request, authenticatedUser.getUser());
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(null);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

}
