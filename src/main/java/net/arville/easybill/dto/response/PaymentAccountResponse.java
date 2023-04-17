package net.arville.easybill.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import net.arville.easybill.model.PaymentAccount;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentAccountResponse {
    private Long id;

    private String paymentAccountLabel;

    private String paymentAccount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static PaymentAccountResponse.PaymentAccountResponseBuilder template(PaymentAccount entity) {
        return PaymentAccountResponse.builder()
                .id(entity.getId())
                .paymentAccountLabel(entity.getPaymentAccountLabel())
                .paymentAccount(entity.getPaymentAccount());
    }

    public static PaymentAccountResponse map(PaymentAccount entity) {
        return PaymentAccountResponse.template(entity)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static PaymentAccountResponse mapWithoutDate(PaymentAccount entity) {
        return PaymentAccountResponse.template(entity).build();
    }
}
