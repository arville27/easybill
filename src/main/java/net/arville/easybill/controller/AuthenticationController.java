package net.arville.easybill.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.UserLoginRequest;
import net.arville.easybill.dto.response.PaymentAccountResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.payload.core.ResponseStatus;
import net.arville.easybill.payload.core.ResponseStructure;
import net.arville.easybill.service.manager.AuthManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static net.arville.easybill.constant.EasybillConstants.AUTH_PATH;

@RestController
@RequestMapping(path = AUTH_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Authentication endpoint")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthManager authManager;

    @PostMapping
    public ResponseEntity<ResponseStructure> authenticateUser(@RequestBody UserLoginRequest authRequest) {

        var authResult = authManager.authenticateUser(authRequest);

        var authResponse = UserResponse
                .template(authResult.getUser())
                .paymentAccountList(authResult.getUser().getPaymentAccountList()
                        .stream()
                        .map(PaymentAccountResponse::map)
                        .toList()
                )
                .accessToken(authResult.getAccessToken())
                .build();

        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(authResponse);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

}
