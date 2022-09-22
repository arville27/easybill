package net.arville.easybill.controller;

import lombok.AllArgsConstructor;
import net.arville.easybill.exception.UserNotFoundException;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.helper.ResponseStatus;
import net.arville.easybill.service.manager.BillManager;
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

    private final BillManager billManager;

    @GetMapping
    public ResponseEntity<ResponseStructure> getAllBills(@PathVariable Long userId) {
        ResponseStructure body;

        try {
            var bills = billManager.getAllBills(userId);
            body = ResponseStatus.SUCCESS.GenerateGeneralBody(bills);
        } catch (UserNotFoundException e) {
            body = ResponseStatus.USER_NOT_FOUND.GenerateGeneralBody(null, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        } catch (Exception e) {
            body = ResponseStatus.UNKNOWN_ERROR.GenerateGeneralBody(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
