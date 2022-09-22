package net.arville.easybill.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.OrderHeaderResponse;
import net.arville.easybill.model.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
public class UserWithOrderResponse {

    private Long id;

    private String username;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private List<OrderHeaderResponse> orderList;

    public static UserWithOrderResponse map(User userEntity, List<OrderHeaderResponse> orderList) {
        UserWithOrderResponse userWithOrderResp = new UserWithOrderResponse();
        userWithOrderResp.setId(userEntity.getId());
        userWithOrderResp.setUsername(userEntity.getUsername());
        userWithOrderResp.setCreatedAt(userEntity.getCreatedAt());
        userWithOrderResp.setUpdatedAt(userEntity.getUpdatedAt());
        userWithOrderResp.setOrderList(orderList);
        return userWithOrderResp;
    }
}
