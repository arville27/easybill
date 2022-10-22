package net.arville.easybill.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.UserRegistrationRequest;
import net.arville.easybill.helper.AuthenticatedUser;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.helper.ResponseStatus;
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

    @GetMapping("/relevant-orders")
    public ResponseEntity<ResponseStructure> getRelevantOrders() {

        var user = userManager.getUserRelevantOrder(authenticatedUser.getUser());
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(user);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/users-orders")
    public ResponseEntity<ResponseStructure> getUsersOrders() {

        var user = userManager.getUsersOrder(authenticatedUser.getUser());
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(user);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping
    public ResponseEntity<ResponseStructure> addNewUser(@RequestBody UserRegistrationRequest request) {

        var newUser = userManager.addNewUser(request);
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

}
