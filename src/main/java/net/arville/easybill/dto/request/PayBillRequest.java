package net.arville.easybill.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.arville.easybill.dto.util.EnsureRequiredFields;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class PayBillRequest implements EnsureRequiredFields {
    private Long userId;
    private BigDecimal amount;

    @Override
    public Set<String> getMissingProperties() {
        Set<String> missingProperties = new LinkedHashSet<>();
        if (userId == null) missingProperties.add("user_id");
        if (amount == null) missingProperties.add("amount");
        return missingProperties;
    }
}
