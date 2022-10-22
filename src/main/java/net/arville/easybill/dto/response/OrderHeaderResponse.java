package net.arville.easybill.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.helper.BillStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderHeaderResponse {

    private Long id;

    private String orderDescription;

    private BigDecimal totalPayment;

    private BigDecimal upto;

    private Double discount;

    private Integer participatingUserCount;

    private BigDecimal totalOrderAmount;

    private BigDecimal otherFee;

    private BigDecimal discountAmount;

    private BigDecimal totalBill;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonProperty("buyer")
    private UserResponse buyerResponse;

    private Long buyerId;

    @JsonProperty("order_list")
    private List<OrderDetailResponse> orderDetailResponses;

    @JsonProperty("order_detail_group_by_user")
    private List<UserResponse> relatedOrderDetail;

    @JsonProperty("user_other_fee")
    private BigDecimal userOtherFee;

    @JsonProperty("order_header_status")
    private BillStatus relevantStatus;

    @JsonProperty("bills")
    private List<BillResponse> billResponse;

    public static OrderHeaderResponseBuilder template(OrderHeader entity) {
        return OrderHeaderResponse.builder()
                .id(entity.getId())
                .upto(entity.getUpto())
                .discount(entity.getDiscount())
                .orderDescription(entity.getOrderDescription())
                .totalPayment(entity.getTotalPayment())
                .totalOrderAmount(entity.getTotalOrderAmount())
                .discountAmount(entity.getDiscountAmount())
                .otherFee(entity.getOtherFee())
                .userOtherFee(entity.getPerUserFee())
                .orderAt(entity.getOrderAt());
    }

    public static OrderHeaderResponse map(OrderHeader entity) {
        return OrderHeaderResponse.template(entity)
                .buyerResponse(UserResponse.mapWithoutDate(entity.getBuyer()))
                .orderDetailResponses(entity.getOrderDetailList().stream().map(OrderDetailResponse::map).collect(Collectors.toList()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
