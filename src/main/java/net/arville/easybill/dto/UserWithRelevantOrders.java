package net.arville.easybill.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.arville.easybill.dto.base.BaseUserEntity;
import net.arville.easybill.model.Bill;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserWithRelevantOrders extends BaseUserEntity {

    public UserWithRelevantOrders fromOriginalEntity(User entity) {
        return UserWithRelevantOrders.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .orderList(entity.getOrderList())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Builder
    private UserWithRelevantOrders(Long id, String username, String password, List<OrderHeader> orderList, List<OrderDetail> orderDetailList, List<Bill> billList, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, username, password, orderList, orderDetailList, billList, createdAt, updatedAt);
    }
}
