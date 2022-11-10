package net.arville.easybill.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.util.ConvertibleToOriginalEntity;
import net.arville.easybill.dto.util.EnsureRequiredFields;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.User;
import net.arville.easybill.model.helper.OrderDetailType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@NoArgsConstructor
@Data
public class OrderDetailRequest implements EnsureRequiredFields, ConvertibleToOriginalEntity<Stream<OrderDetail>> {
    private String orderMenuDesc;

    private Integer qty;

    private BigDecimal price;

    private List<User> users;

    @Override
    public Stream<OrderDetail> toOriginalEntity() {
        BigDecimal orderUserCount = BigDecimal.valueOf(this.users.size());
        OrderDetailType orderType = this.users.size() > 1 ? OrderDetailType.MULTI_USER : OrderDetailType.SINGLE_USER;
        BigDecimal totalOrderDetail = this.price.multiply(BigDecimal.valueOf(this.qty));
        return users.stream().map(u -> OrderDetail.builder()
                .user(u)
                .orderMenuDesc(orderMenuDesc)
                .orderType(orderType)
                .qty(qty)
                .price(totalOrderDetail.divide(orderUserCount, 0, RoundingMode.HALF_UP))
                .build()
        );
    }

    @Override
    public Set<String> getMissingProperties() {
        Set<String> missingProperties = new LinkedHashSet<>();
        if (orderMenuDesc == null) missingProperties.add("order_menu_desc");
        if (qty == null) missingProperties.add("qty");
        if (price == null) missingProperties.add("price");
        return missingProperties;
    }
}
