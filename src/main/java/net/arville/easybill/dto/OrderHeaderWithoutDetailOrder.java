package net.arville.easybill.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.util.ConvertibleFromOriginalEntitiy;
import net.arville.easybill.model.OrderHeader;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class OrderHeaderWithoutDetailOrder implements ConvertibleFromOriginalEntitiy<OrderHeaderWithoutDetailOrder, OrderHeader> {

    private Long buyerId;

    private String orderDescription;

    private Double discount;

    private BigDecimal upto;

    private BigDecimal totalPayment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderAt;

    @Override
    public OrderHeaderWithoutDetailOrder fromOriginalEntity(OrderHeader entity) {
        var orderHeaderWithoutDetailOrder = new OrderHeaderWithoutDetailOrder();
        orderHeaderWithoutDetailOrder.setBuyerId(entity.getUser().getId());
        orderHeaderWithoutDetailOrder.setOrderDescription(entity.getOrderDescription());
        orderHeaderWithoutDetailOrder.setDiscount(entity.getDiscount());
        orderHeaderWithoutDetailOrder.setUpto(entity.getUpto());
        orderHeaderWithoutDetailOrder.setTotalPayment(entity.getTotalPayment());
        orderHeaderWithoutDetailOrder.setOrderAt(entity.getOrderAt());
        return orderHeaderWithoutDetailOrder;
    }
}
