package net.arville.easybill.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import net.arville.easybill.model.BillTransaction;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillTransactionResponse {

    private UserResponse payer;
    private UserResponse receiver;
    private BigDecimal paidAmount;

    public static BillTransactionResponseBuilder template(BillTransaction entity) {
        return BillTransactionResponse
                .builder()
                .payer(UserResponse.mapWithoutDate(entity.getPayer()))
                .receiver(UserResponse.mapWithoutDate(entity.getReceiver()))
                .paidAmount(entity.getAmount());
    }

    public static BillTransactionResponse map(BillTransaction entity) {
        return BillTransactionResponse.template(entity).build();
    }

}
