package net.arville.easybill.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.PayBillRequest;
import net.arville.easybill.helper.AuthenticatedUser;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.helper.ResponseStatus;
import net.arville.easybill.service.manager.BillTransactionManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/bill-transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "User's Bill Transaction", description = "User bill transactions related resource")
@SecurityRequirement(name = "Access Token")
@RequiredArgsConstructor
public class BillTransactionController {
    private final AuthenticatedUser authenticatedUser;
    private final BillTransactionManager billTransactionManager;

    @GetMapping("/history")
    public ResponseEntity<ResponseStructure> getAllStatusTransaction() {

        var billTransactions = billTransactionManager.getRelevantUsersBillTransaction(authenticatedUser.getUser());
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(billTransactions);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping
    public ResponseEntity<ResponseStructure> payBills(@RequestBody PayBillRequest payBillRequest) {

        var result = billTransactionManager.payUnpaidBills(authenticatedUser.getUser(), payBillRequest);
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(result);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

}
