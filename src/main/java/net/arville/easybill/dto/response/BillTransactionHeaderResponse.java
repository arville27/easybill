package net.arville.easybill.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import net.arville.easybill.model.BillTransactionHeader;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillTransactionHeaderResponse {

    private Long id;

    @JsonProperty("contributed_amount")
    private BigDecimal paidAmount;

    @JsonProperty("related_order_header")
    private OrderHeaderResponse orderHeaderResponse;

    public static BillTransactionHeaderResponseBuilder template(BillTransactionHeader entity) {
        return BillTransactionHeaderResponse
                .builder()
                .paidAmount(entity.getPaidAmount());
    }

    public static BillTransactionHeaderResponse map(BillTransactionHeader entity) {
        return BillTransactionHeaderResponse
                .template(entity)
                .build();
    }

    public static BillTransactionHeaderResponse mapWithoutDate(BillTransactionHeader entity) {
        return BillTransactionHeaderResponse.template(entity).build();
    }
}
