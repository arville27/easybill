package net.arville.easybill.controller;

import lombok.AllArgsConstructor;
import net.arville.easybill.helper.AuthenticatedUser;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.helper.ResponseStatus;
import net.arville.easybill.service.manager.BillManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/bills")
@AllArgsConstructor
public class BillController {
    private final BillManager billManager;
    private final AuthenticatedUser authenticatedUser;

    @GetMapping
    public ResponseEntity<ResponseStructure> getAllBills() {

        var bills = billManager.getAllBills(authenticatedUser.getUserId());
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(bills);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
