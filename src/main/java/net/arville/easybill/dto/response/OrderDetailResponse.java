package net.arville.easybill.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.arville.easybill.dto.base.BaseOrderDetailEntity;
import net.arville.easybill.dto.helper.EntityBuilder;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.User;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailResponse extends BaseOrderDetailEntity {
    @JsonProperty("user")
    private UserResponse userData;

    private Long userId;

    public static OrderDetailResponse.OrderDetailResponseBuilder template(OrderDetail entity) {
        return OrderDetailResponse.builder()
                .id(entity.getId())
                .itemDiscount(entity.getItemDiscount())
                .orderMenuDesc(entity.getOrderMenuDesc())
                .price(entity.getPrice())
                .qty(entity.getQty());
    }

    public static OrderDetailResponse map(OrderDetail entity) {
        return OrderDetailResponse.template(entity)
                .userData(UserResponse.mapWithoutDate(entity.getUser()))
                .build();
    }

    public static OrderDetailResponse customMap(
            OrderDetail entity,
            EntityBuilder<OrderDetailResponse, OrderDetailResponse.OrderDetailResponseBuilder, OrderDetail> builder
    ) {
        return builder.createCustomEntity(OrderDetailResponse.builder(), entity);
    }

    @Builder
    public OrderDetailResponse(Long id, String orderMenuDesc, BigDecimal price, BigDecimal itemDiscount, Integer qty, User user, UserResponse userData, Long userId) {
        super(id, orderMenuDesc, price, itemDiscount, qty, user);
        this.userData = userData;
        this.userId = userId;
    }
}
