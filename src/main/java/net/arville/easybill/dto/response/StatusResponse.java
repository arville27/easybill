package net.arville.easybill.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import net.arville.easybill.dto.helper.EntityBuilder;
import net.arville.easybill.model.Status;
import net.arville.easybill.model.helper.BillStatus;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusResponse {

    private Long orderHeaderId;

    @JsonProperty("user")
    private UserResponse userResponse;

    private BillStatus status;

    private BigDecimal oweAmount;

    public static StatusResponseBuilder template(Status entity) {
        return StatusResponse.builder()
                .orderHeaderId(entity.getOrderHeader().getId())
                .userResponse(UserResponse.mapWithoutDate(entity.getUser()))
                .status(entity.getStatus())
                .oweAmount(entity.getOweAmount());
    }

    public static StatusResponse map(Status entity) {
        return StatusResponse.template(entity).build();
    }

    public static StatusResponse customMap(
            Status entity,
            EntityBuilder<StatusResponse, StatusResponseBuilder, Status> builder
    ) {
        return builder.createCustomEntity(StatusResponse.builder(), entity);
    }
}
