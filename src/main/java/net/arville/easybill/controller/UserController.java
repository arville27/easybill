package net.arville.easybill.controller;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.UserRegistrationRequest;
import net.arville.easybill.exception.UserNotFoundException;
import net.arville.easybill.model.User;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.helper.ResponseStatus;
import net.arville.easybill.service.UserServices;
import org.springframework.core.env.MissingRequiredPropertiesException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private UserServices userServices;

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseStructure> getUser(@PathVariable Long userId) {
        ResponseStructure body;

        try {
            User user = userServices.getUser(userId);
            body = ResponseStatus.SUCCESS.GenerateGeneralBody(user);
        } catch (UserNotFoundException e) {
            body = ResponseStatus.NOT_FOUND.GenerateGeneralBody(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping
    public ResponseEntity<ResponseStructure> addNewUser(@RequestBody UserRegistrationRequest request) {
        ResponseStructure body;

        try {
            var newUser = userServices.addNewUser(request);
            body = ResponseStatus.SUCCESS.GenerateGeneralBody(newUser);
        } catch (MissingRequiredPropertiesException e) {
            body = ResponseStatus.MISSING_REQUIRED_FIELDS.GenerateGeneralBody(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            body = ResponseStatus.UNKNOWN_ERROR.GenerateGeneralBody(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

}
