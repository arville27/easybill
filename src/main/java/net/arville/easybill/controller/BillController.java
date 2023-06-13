package net.arville.easybill.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.response.BillResponse;
import net.arville.easybill.dto.response.OrderHeaderResponse;
import net.arville.easybill.dto.response.PaymentAccountResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.helper.AuthenticatedUser;
import net.arville.easybill.model.User;
import net.arville.easybill.model.helper.BillStatus;
import net.arville.easybill.payload.core.ResponseStatus;
import net.arville.easybill.payload.core.ResponseStructure;
import net.arville.easybill.service.manager.BillManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/bills", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "User's Bill", description = "User bills related resource")
@SecurityRequirement(name = "Access Token")
@RequiredArgsConstructor
public class BillController {
    private final AuthenticatedUser authenticatedUser;
    private final BillManager billManager;

    @GetMapping("/payable")
    public ResponseEntity<ResponseStructure> getUserPayable() {

        var payables = billManager.getUserPayables(authenticatedUser.getUser());
        var response = this.generateBillResponse(authenticatedUser.getUser(), payables, false);
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(response);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/receivables")
    public ResponseEntity<ResponseStructure> gerUserReceivable() {

        var receivables = billManager.getUserReceivables(authenticatedUser.getUser());
        var response = this.generateBillResponse(authenticatedUser.getUser(), receivables, true);
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(response);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    private UserResponse generateBillResponse(
            User billParent,
            Map<User, BillResponse.AggregatedRelatedOrderWithTotalOwe> aggregated,
            boolean isReceivable
    ) {
        var listBills = aggregated.entrySet()
                .stream()
                .map(userMapEntry -> {
                    User buyer = userMapEntry.getKey();
                    var totalOwe = userMapEntry.getValue().getTotalOweAmount();
                    var orderHeaderList = userMapEntry.getValue().getRelatedOrderHeader();
                    return BillResponse.builder()
                            .userResponse(UserResponse.template(buyer)
                                    .paymentAccountList(buyer.getPaymentAccountList()
                                            .stream()
                                            .map(PaymentAccountResponse::mapWithoutDate)
                                            .toList()
                                    )
                                    .build()
                            )
                            .oweAmount(totalOwe)
                            .relatedOrderHeader(orderHeaderList
                                    .stream()
                                    .map(orderHeader -> OrderHeaderResponse
                                            .template(orderHeader)
                                            .buyerResponse(UserResponse.mapWithoutDate(orderHeader.getBuyer()))
                                            .totalBill(orderHeader.getRelevantBill(isReceivable ? buyer : billParent).getOweAmountWithBillTransaction())
                                            .build()
                                    )
                                    .toList()
                            )
                            .status(BillStatus.UNPAID)
                            .build();
                })
                .toList();

        return UserResponse.template(billParent)
                .billResponseList(listBills)
                .build();
    }

}
