package net.arville.easybill.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.arville.easybill.helper.AuthenticatedUser;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.helper.ResponseStatus;
import net.arville.easybill.service.manager.BillManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/bills", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "User's Bill", description = "User bills related resource")
@SecurityRequirement(name = "Access Token")
@RequiredArgsConstructor
public class BillController {
    private final AuthenticatedUser authenticatedUser;
    private final BillManager billManager;

    @GetMapping("/payable")
    public ResponseEntity<ResponseStructure> getAllBills() {

        var bills = billManager.getAllUsersBill(authenticatedUser.getUser());
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(bills);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/receivables")
    public ResponseEntity<ResponseStructure> getAllBillsToUser() {

        var bills = billManager.getAllUsersBillToUser(authenticatedUser.getUser());
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(bills);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

}
