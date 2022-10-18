package net.arville.easybill.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.arville.easybill.dto.base.BaseUserEntity;
import net.arville.easybill.dto.helper.EntityBuilder;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse extends BaseUserEntity {

    @JsonProperty("order_list")
    private List<OrderHeaderResponse> orderHeaderResponseList;

    @JsonProperty("total_price")
    private BigDecimal totalOrder;

    @JsonProperty("discount_total")
    private BigDecimal discountTotal;
    
    @JsonProperty("total_price_after_discount")
    private BigDecimal totalOrderAfterDiscount;

    @JsonProperty("user_orders")
    private List<OrderDetailResponse> userOrders;
    private String accessToken;
    @JsonProperty("users_bills")
    private List<StatusResponse> statusResponseList;

    public static UserResponse.UserResponseBuilder template(User entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername());
    }

    public static UserResponse map(User entity) {
        return UserResponse.template(entity)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static UserResponse mapWithoutDate(User entity) {
        return UserResponse.template(entity).build();
    }

    public static UserResponse customMap(
            User entity,
            EntityBuilder<UserResponse, UserResponse.UserResponseBuilder, User> builder
    ) {
        return builder.createCustomEntity(UserResponse.builder(), entity);
    }

    @Builder
    public UserResponse(Long id, String username, String password, List<OrderHeader> orderList, List<OrderDetail> orderDetailList, LocalDateTime createdAt, LocalDateTime updatedAt, List<OrderHeaderResponse> orderHeaderResponseList, BigDecimal totalOrder, BigDecimal discountTotal, BigDecimal totalOrderAfterDiscount, List<OrderDetailResponse> userOrders, String accessToken, List<StatusResponse> statusResponseList) {
        super(id, username, password, orderList, orderDetailList, createdAt, updatedAt);
        this.orderHeaderResponseList = orderHeaderResponseList;
        this.totalOrder = totalOrder;
        this.discountTotal = discountTotal;
        this.totalOrderAfterDiscount = totalOrderAfterDiscount;
        this.userOrders = userOrders;
        this.accessToken = accessToken;
        this.statusResponseList = statusResponseList;
    }
}
