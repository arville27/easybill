package net.arville.easybill.controller;

import lombok.AllArgsConstructor;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.helper.ResponseStatus;
import net.arville.easybill.service.BillServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}/bills")
@AllArgsConstructor
public class BillController {

    private final BillServices billServices;

    @GetMapping
    public ResponseEntity<ResponseStructure> getAllBills(@PathVariable Long userId) {
        ResponseStructure body;

        try {
            var bills = billServices.getAllBills(userId);
            body = ResponseStatus.SUCCESS.GenerateGeneralBody(bills);
        } catch (Exception e) {
            body = ResponseStatus.UNKNOWN_ERROR.GenerateGeneralBody(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
