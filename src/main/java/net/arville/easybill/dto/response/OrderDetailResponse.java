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
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailResponse extends BaseOrderDetailEntity {
    @JsonProperty("user")
    private UserResponse userData;

    private Long userId;

    public static OrderDetailResponse map(OrderDetail entity) {
        return OrderDetailResponse.builder()
                .id(entity.getId())
                .userData(UserResponse.mapWithoutDate(entity.getUser()))
                .orderMenuDesc(entity.getOrderMenuDesc())
                .price(entity.getPrice())
                .qty(entity.getQty())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static OrderDetailResponse mapWithoutDate(OrderDetail entity) {
        return OrderDetailResponse.builder()
                .id(entity.getId())
                .userData(UserResponse.mapWithoutDate(entity.getUser()))
                .orderMenuDesc(entity.getOrderMenuDesc())
                .price(entity.getPrice())
                .qty(entity.getQty())
                .build();
    }

    public static OrderDetailResponse customMap(
            OrderDetail entity,
            EntityBuilder<OrderDetailResponse, OrderDetailResponse.OrderDetailResponseBuilder, OrderDetail> builder
    ) {
        return builder.createCustomEntity(OrderDetailResponse.builder(), entity);
    }

    @Builder
    public OrderDetailResponse(Long id, String orderMenuDesc, BigDecimal price, Integer qty, User user, LocalDateTime createdAt, LocalDateTime updatedAt, UserResponse userData, Long userId) {
        super(id, orderMenuDesc, price, qty, user, createdAt, updatedAt);
        this.userData = userData;
        this.userId = userId;
    }
}
