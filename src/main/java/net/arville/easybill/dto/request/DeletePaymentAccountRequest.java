package net.arville.easybill.dto.request;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class DeletePaymentAccountRequest {
    private String currentPassword;
}
