package net.arville.easybill.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import net.arville.easybill.model.BillTransaction;
import net.arville.easybill.model.helper.BillTransactionOrigin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private BillTransactionOrigin origin;

    @JsonProperty("bill_transaction_header_list")
    private List<BillTransactionHeaderResponse> billTransactionHeaderResponseList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static BillTransactionResponseBuilder template(BillTransaction entity) {
        return BillTransactionResponse
                .builder()
                .payer(UserResponse.mapWithoutDate(entity.getPayer()))
                .receiver(UserResponse.mapWithoutDate(entity.getReceiver()))
                .origin(entity.getOrigin())
                .paidAmount(entity.getAmount());
    }

    public static BillTransactionResponse map(BillTransaction entity) {
        return BillTransactionResponse
                .template(entity)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static BillTransactionResponse mapWithoutDate(BillTransaction entity) {
        return BillTransactionResponse.template(entity).build();
    }

}
