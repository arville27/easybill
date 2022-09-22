package net.arville.easybill.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.util.ConvertibleToOriginalEntity;
import net.arville.easybill.dto.util.EnsureRequiredFields;
import net.arville.easybill.model.OrderDetail;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor
@Data
public class OrderDetailRequest implements EnsureRequiredFields, ConvertibleToOriginalEntity<OrderDetail> {

    private Long userId;

    private String orderMenuDesc;

    private Integer qty;

    private BigDecimal price;

    @Override
    public OrderDetail toOriginalEntity() {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderMenuDesc(orderMenuDesc);
        orderDetail.setQty(qty);
        orderDetail.setPrice(price);
        return orderDetail;
    }

    @Override
    public Set<String> getMissingProperties() {
        Set<String> missingProperties = new LinkedHashSet<>();
        if (userId == null) missingProperties.add("user_id");
        if (orderMenuDesc == null) missingProperties.add("order_menu_desc");
        if (qty == null) missingProperties.add("qty");
        if (price == null) missingProperties.add("price");
        return missingProperties;
    }
}
