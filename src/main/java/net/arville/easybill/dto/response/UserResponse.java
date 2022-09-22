package net.arville.easybill.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.arville.easybill.dto.base.BaseUserEntity;
import net.arville.easybill.dto.helper.EntityBuilder;
import net.arville.easybill.model.Bill;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse extends BaseUserEntity {

    @JsonProperty("order_list")
    private List<OrderHeaderResponse> orderHeaderResponseList;

    public static UserResponse map(User entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static UserResponse mapWithoutDate(User entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .build();
    }

    public static UserResponse customMap(
            User entity,
            EntityBuilder<UserResponse, UserResponse.UserResponseBuilder, User> builder
    ) {
        return builder.createCustomEntity(UserResponse.builder(), entity);
    }

    @Builder
    public UserResponse(Long id, String username, String password, List<OrderHeader> orderList, List<OrderDetail> orderDetailList, List<Bill> billList, LocalDateTime createdAt, LocalDateTime updatedAt, List<OrderHeaderResponse> orderHeaderResponseList) {
        super(id, username, password, orderList, orderDetailList, billList, createdAt, updatedAt);
        this.orderHeaderResponseList = orderHeaderResponseList;
    }
}
