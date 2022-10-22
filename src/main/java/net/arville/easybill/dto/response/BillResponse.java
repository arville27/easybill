package net.arville.easybill.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import net.arville.easybill.model.Bill;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.helper.BillStatus;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillResponse {

    private Long orderHeaderId;

    @JsonProperty("user")
    private UserResponse userResponse;

    @JsonProperty("order_header")
    private OrderHeaderResponse orderHeaderResponse;

    @JsonProperty("related_order_header")
    private List<OrderHeaderResponse> relatedOrderHeader;

    private BigDecimal totalPaid;

    private BillStatus status;

    private BigDecimal oweAmount;

    public static BillResponseBuilder template(Bill entity) {
        return BillResponse.builder()
                .userResponse(UserResponse.mapWithoutDate(entity.getUser()))
                .status(entity.getStatus())
                .totalPaid(entity.getTotalPaidAmount())
                .oweAmount(entity.getOweAmount());
    }

    public static BillResponse map(Bill entity) {
        return BillResponse.template(entity).build();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class AggregatedRelatedOrderWithTotalOwe {
        private List<OrderHeader> relatedOrderHeader;
        private BigDecimal totalOweAmount;
    }
}
