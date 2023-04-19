package net.arville.easybill.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.UserChangeAccountNumberRequest;
import net.arville.easybill.dto.response.PaymentAccountResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.helper.AuthenticatedUser;
import net.arville.easybill.payload.core.ResponseStatus;
import net.arville.easybill.payload.core.ResponseStructure;
import net.arville.easybill.service.manager.PaymentAccountManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/payment-account", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Payment Account", description = "User's payment account resource")
@SecurityRequirement(name = "Access Token")
@RequiredArgsConstructor
public class PaymentAccountController {
    private final PaymentAccountManager paymentAccountManager;
    private final AuthenticatedUser authenticatedUser;

    @PostMapping
    public ResponseEntity<ResponseStructure> saveUserPaymentAccount(
            @RequestBody UserChangeAccountNumberRequest request
    ) {

        var updatedUserData = paymentAccountManager.savePaymentAccount(
                request,
                authenticatedUser.getUser()
        );

        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(
                UserResponse.template(updatedUserData)
                        .paymentAccountList(updatedUserData.getPaymentAccountList()
                                .stream()
                                .map(PaymentAccountResponse::map)
                                .toList()
                        )
                        .build()
        );

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @DeleteMapping("{paymentAccountId}")
    public ResponseEntity<ResponseStructure> deletePaymentAccount(
            @PathVariable Long paymentAccountId
    ) {

        paymentAccountManager.deletePaymentAccount(
                paymentAccountId,
                authenticatedUser.getUser()
        );

        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(null);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
