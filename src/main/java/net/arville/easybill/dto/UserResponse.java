package net.arville.easybill.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse extends BaseUserEntity {

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

    @Builder
    public UserResponse(Long id, String username, String password, List<OrderHeader> orderList, List<OrderDetail> orderDetailList, List<Bill> billList, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, username, password, orderList, orderDetailList, billList, createdAt, updatedAt);
    }
}
