package net.arville.easybill.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.util.ConvertibleToOriginalEntity;
import net.arville.easybill.dto.util.EnsureRequiredFields;
import net.arville.easybill.model.OrderHeader;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Data
public class AddOrderRequest implements EnsureRequiredFields, ConvertibleToOriginalEntity<OrderHeader> {

    private String orderDescription;

    private BigDecimal totalPayment;

    private BigDecimal upto;

    private Double discount;

    private Long buyerId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderAt;

    private List<OrderDetailRequest> orderList;

    @Override
    public Set<String> getMissingProperties() {
        Set<String> missingProperties = new LinkedHashSet<>();
        if (orderDescription == null) missingProperties.add("order_description");
        if (totalPayment == null) missingProperties.add("total_payment");
        if (upto == null) missingProperties.add("upto");
        if (discount == null) missingProperties.add("discount");
        if (orderAt == null) missingProperties.add("order_at");
        if (buyerId == null) missingProperties.add("buyer_id");
        if (orderList == null) missingProperties.add("order_list");
        return missingProperties;
    }

    @Override
    public OrderHeader toOriginalEntity() {
        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setOrderDescription(orderDescription);
        orderHeader.setTotalPayment(totalPayment);
        orderHeader.setUpto(upto);
        orderHeader.setDiscount(discount / 10);
        orderHeader.setOrderAt(orderAt);
        return orderHeader;
    }
}
