package net.arville.easybill.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.arville.easybill.dto.base.BaseBillEntity;
import net.arville.easybill.dto.helper.EntityBuilder;
import net.arville.easybill.model.Bill;
import net.arville.easybill.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillResponse extends BaseBillEntity {

    @JsonProperty("user")
    private UserResponse userData;

    @JsonProperty("owe")
    private UserResponse oweUser;

    public static BillResponse.BillResponseBuilder template(Bill entity) {
        return BillResponse.builder()
                .id(entity.getId())
                .userData(UserResponse.mapWithoutDate(entity.getUser()))
                .oweUser(UserResponse.mapWithoutDate(entity.getOwe()))
                .oweTotal(entity.getOweTotal());
    }

    public static BillResponse map(Bill entity) {
        return BillResponse.template(entity)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static BillResponse mapWithoutDate(Bill entity) {
        return BillResponse.template(entity).build();
    }

    public static BillResponse customMap(
            Bill entity,
            EntityBuilder<BillResponse, BillResponse.BillResponseBuilder, Bill> builder
    ) {
        return builder.createCustomEntity(BillResponse.builder(), entity);
    }

    @Builder
    public BillResponse(Long id, User user, User owe, BigDecimal oweTotal, LocalDateTime createdAt, LocalDateTime updatedAt, UserResponse userData, UserResponse oweUser) {
        super(id, user, owe, oweTotal, createdAt, updatedAt);
        this.userData = userData;
        this.oweUser = oweUser;
    }
}
