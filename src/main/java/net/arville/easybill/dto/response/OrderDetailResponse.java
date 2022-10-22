package net.arville.easybill.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import net.arville.easybill.model.OrderDetail;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailResponse {

    // Base properties of the model
    private Long id;

    private String orderMenuDesc;

    private BigDecimal price;

    private BigDecimal itemDiscount;

    private Integer qty;

    @JsonProperty("user")
    private UserResponse userData;

    private Long userId;

    public static OrderDetailResponseBuilder template(OrderDetail entity) {
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
}
