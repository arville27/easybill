package net.arville.easybill.controller;

import lombok.AllArgsConstructor;
import net.arville.easybill.exception.UserNotFoundException;
import net.arville.easybill.model.User;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.helper.ResponseStatus;
import net.arville.easybill.service.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
