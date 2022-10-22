package net.arville.easybill.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import net.arville.easybill.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;

    private String username;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

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
    private List<BillResponse> billResponseList;

    @JsonProperty("bill_transaction_list")
    private List<BillTransactionResponse> billTransactionResponseList;

    public static UserResponseBuilder template(User entity) {
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
}
