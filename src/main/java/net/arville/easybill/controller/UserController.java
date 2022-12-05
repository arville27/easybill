package net.arville.easybill.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.UserChangeAccountNumberRequest;
import net.arville.easybill.dto.request.UserChangePasswordRequest;
import net.arville.easybill.dto.request.UserRegistrationRequest;
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
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(user);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/relevant-orders")
    public ResponseEntity<ResponseStructure> getRelevantOrders(
            @RequestParam(name = "page", required = false, defaultValue = "1") int pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(name = "status", required = false) String orderStatus
    ) {

        var user = userManager.getUserRelevantOrder(
                authenticatedUser.getUser(),
                pageNumber,
                pageSize,
                keyword,
                orderStatus
        );
        ResponseStructure body = ResponseStatus.SUCCESS.GeneratePaginationBody(user);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/users-orders")
    public ResponseEntity<ResponseStructure> getUsersOrders(
            @RequestParam(name = "page", required = false, defaultValue = "1") int pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(name = "status", required = false) String orderStatus
    ) {

        var user = userManager.getUsersOrder(
                authenticatedUser.getUser(),
                pageNumber,
                pageSize,
                keyword,
                orderStatus
        );
        ResponseStructure body = ResponseStatus.SUCCESS.GeneratePaginationBody(user);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping
    public ResponseEntity<ResponseStructure> addNewUser(@RequestBody UserRegistrationRequest request) {

        var newUser = userManager.addNewUser(request);
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PutMapping("/password")
    public ResponseEntity<ResponseStructure> changeUserPassword(@RequestBody UserChangePasswordRequest request) {

        userManager.changeUserPassword(request, authenticatedUser.getUser());
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(null);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PutMapping("/account-number")
    public ResponseEntity<ResponseStructure> changeUserAccountNumber(@RequestBody UserChangeAccountNumberRequest request) {

        userManager.changeUserAccountNumber(request, authenticatedUser.getUser());
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(null);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

}
