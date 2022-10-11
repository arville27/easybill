package net.arville.easybill.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.arville.easybill.dto.base.BaseOrderHeaderEntity;
import net.arville.easybill.dto.helper.EntityBuilder;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderHeaderResponse extends BaseOrderHeaderEntity {

    @JsonProperty("user")
    private UserResponse userResponse;
    private Long buyerId;
    @JsonProperty("order_list")
    private List<OrderDetailResponse> orderDetailResponses;

    public static OrderHeaderResponse.OrderHeaderResponseBuilder template(OrderHeader entity) {
        return OrderHeaderResponse.builder()
                .id(entity.getId())
                .upto(entity.getUpto())
                .participatingUserCount(entity.getParticipatingUserCount())
                .discount(entity.getDiscount())
                .orderDescription(entity.getOrderDescription())
                .totalPayment(entity.getTotalPayment())
                .totalOrderAmount(entity.getTotalOrderAmount())
                .discountAmount(entity.getDiscountAmount())
                .otherFee(entity.getOtherFee())
                .orderAt(entity.getOrderAt());
    }

    public static OrderHeaderResponse map(OrderHeader entity) {
        return OrderHeaderResponse.template(entity)
                .userResponse(UserResponse.mapWithoutDate(entity.getUser()))
                .orderDetailResponses(entity.getOrderDetailList().stream().map(OrderDetailResponse::map).collect(Collectors.toList()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static OrderHeaderResponse mapWithoutDate(OrderHeader entity) {
        return OrderHeaderResponse.template(entity)
                .userResponse(UserResponse.mapWithoutDate(entity.getUser()))
                .orderDetailResponses(entity.getOrderDetailList().stream().map(OrderDetailResponse::map).collect(Collectors.toList()))
                .build();
    }

    public static OrderHeaderResponse customMap(
            OrderHeader entity,
            EntityBuilder<OrderHeaderResponse, OrderHeaderResponse.OrderHeaderResponseBuilder, OrderHeader> builder
    ) {
        return builder.createCustomEntity(OrderHeaderResponse.builder(), entity);
    }

    @Builder

    public OrderHeaderResponse(Long id, String orderDescription, BigDecimal totalPayment, User user, BigDecimal upto, Double discount, Integer participatingUserCount, BigDecimal totalOrderAmount, BigDecimal otherFee, BigDecimal discountAmount, List<OrderDetail> orderDetailList, LocalDateTime orderAt, LocalDateTime createdAt, LocalDateTime updatedAt, UserResponse userResponse, Long buyerId, List<OrderDetailResponse> orderDetailResponses) {
        super(id, orderDescription, totalPayment, user, upto, discount, participatingUserCount, totalOrderAmount, otherFee, discountAmount, orderDetailList, orderAt, createdAt, updatedAt);
        this.userResponse = userResponse;
        this.buyerId = buyerId;
        this.orderDetailResponses = orderDetailResponses;
    }
}
