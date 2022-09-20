package net.arville.easybill.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.util.ConvertibleFromOriginalEntitiy;
import net.arville.easybill.model.OrderHeader;

import java.math.BigDecimal;

@NoArgsConstructor
@Data
public class OrderHeaderWithoutDetailOrder implements ConvertibleFromOriginalEntitiy<OrderHeaderWithoutDetailOrder, OrderHeader> {

    private Long buyerId;

    private String orderDescription;

    private Double discount;

    private BigDecimal upto;

    private BigDecimal totalPayment;

    @Override
    public OrderHeaderWithoutDetailOrder fromOriginalEntity(OrderHeader entity) {
        var orderHeaderWithoutDetailOrder = new OrderHeaderWithoutDetailOrder();
        orderHeaderWithoutDetailOrder.setBuyerId(entity.getUser().getId());
        orderHeaderWithoutDetailOrder.setOrderDescription(entity.getOrderDescription());
        orderHeaderWithoutDetailOrder.setDiscount(entity.getDiscount());
        orderHeaderWithoutDetailOrder.setUpto(entity.getUpto());
        orderHeaderWithoutDetailOrder.setTotalPayment(entity.getTotalPayment());
        return orderHeaderWithoutDetailOrder;
    }
}
