package net.arville.easybill.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import net.arville.easybill.helper.AuthenticatedUser;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.helper.ResponseStatus;
import net.arville.easybill.service.manager.StatusManager;
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
@AllArgsConstructor
public class StatusController {
    private final AuthenticatedUser authenticatedUser;
    private final StatusManager statusManager;

    @GetMapping("/payable")
    public ResponseEntity<ResponseStructure> getAllStatus() {

        var status = statusManager.getAllUsersBill(authenticatedUser.getUser());
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(status);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/receivables")
    public ResponseEntity<ResponseStructure> getAllStatusToUser() {

        var status = statusManager.getAllUsersBillToUser(authenticatedUser.getUser());
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(status);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/history")
    public ResponseEntity<ResponseStructure> getAllStatusTransaction() {

        var status = statusManager.getAllUsersBillToUser(authenticatedUser.getUser());
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(status);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

}
